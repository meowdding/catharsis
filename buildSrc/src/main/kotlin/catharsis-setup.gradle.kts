@file:Suppress("UnstableApiUsage")

import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    idea
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("versioned-catalogues")
    id("me.owdding.auto-mixins")
}

private val stonecutter = project.extensions.getByName("stonecutter") as StonecutterBuildExtension
fun isUnobfuscated() = stonecutter.eval(stonecutter.current.version, ">=26.1")

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven("https://maven.teamresourceful.com/repository/maven-public/", "tech.thatgravyboat", "me.owdding")
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    scopedMaven("https://raw.githubusercontent.com/fishstiz/maven/m2", "io.github.fishstiz")
    mavenCentral()
    mavenLocal()
}

val accessWidenerFile = rootProject.file("src/catharsis${if (isUnobfuscated()) "" else ".obf"}.accesswidener")
val mcVersion = stonecutter.current.version.replace(".", "")
val loom = extensions.getByName<LoomGradleExtensionAPI>("loom")
loom.apply {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=${rootProject.projectDir.resolve("run/${mcVersion}Mods").absolutePath}")
    }

    accessWidenerPath.set(accessWidenerFile)
}

ksp {
    arg("meowdding.project_name", "catharsis")
    arg("meowdding.package", "me.owdding.catharsis.generated")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(if (isUnobfuscated()) 25 else 21)
}

kotlin {
    jvmToolchain(if (isUnobfuscated()) 25 else 21)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(if (isUnobfuscated()) 25 else 21)
    withSourcesJar()
}

val archiveName = "Catharsis"

base {
    archivesName.set("$archiveName-${archivesName.get()}")
}

tasks.named("build") {
    doLast {
        val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/${archiveName}-${stonecutter.current.version}-$version.jar")
        val targetFile = rootProject.projectDir.resolve("build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(if (isUnobfuscated()) 25 else 21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(if (isUnobfuscated()) JvmTarget.JVM_25 else JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.add("-Xnullability-annotations=@org.jspecify.annotations:warn")
    compilerOptions.freeCompilerArgs.add("-Xwhen-guards")
}

val gitRef = tasks.register<Exec>("gitRef") {
    outputs.upToDateWhen { false }
    standardOutput = ByteArrayOutputStream()
    commandLine("git", "rev-parse", "HEAD")
}

val gitBranch = tasks.register<Exec>("getBranch") {
    outputs.upToDateWhen { false }
    standardOutput = ByteArrayOutputStream()
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}

tasks.processResources {
    val buildRepo = tasks.getByPath(":repo:buildRepo")
    dependsOn(gitRef, gitBranch, buildRepo)
    mustRunAfter(gitRef, gitBranch, buildRepo)
    val range = if (versionedCatalog.versions.has("minecraft.range")) {
        versionedCatalog.versions.get("minecraft.range").toString()
    } else {
        val start = versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft")
        val end = versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft")
        ">=$start <=$end"
    }
    val replacements = mapOf(
        "version" to version,
        "minecraft_range" to range,
        "fabric_lang_kotlin" to versionedCatalog.versions["fabric.language.kotlin"],
        "sbapi" to versionedCatalog.versions["skyblockapi"],
    )

    outputs.upToDateWhen { false }
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }

    filesMatching("catharsis.json") {
        expand(
            "branch" to gitBranch.map { it.standardOutput.toString().substringBefore("\n") }.get(),
            "ref" to gitRef.map { it.standardOutput.toString().substringBefore("\n") }.get(),
            "build_time" to provider { System.currentTimeMillis() }.get(),
        )
    }

    with(copySpec {
        from(buildRepo.outputs)
        into("repo")
    })
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    with(copySpec {
        from(accessWidenerFile)
        rename { it.replace(".obf", "") }
    })
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }

val fat by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false

    extendsFrom(configurations["runtimeClasspath"], configurations["compileClasspath"])
}

val unobfuscatedBuild by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts.add("unobfuscatedBuild", tasks.jar)

fun makeAlias(configuration: String) = if (isUnobfuscated()) configuration else "mod" + configuration.replaceFirstChar { it.uppercase() }

val maybeModImplementation = makeAlias("implementation")
val maybeModCompileOnly = makeAlias("compileOnly")
val maybeModRuntimeOnly = makeAlias("runtimeOnly")
val maybeModApi = makeAlias("api")
val maybeModLocalRuntime = makeAlias("localRuntime")

dependencies {
    "minecraft"(versionedCatalog["minecraft"])

    "api"(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}${if (isUnobfuscated()) "" else "-remapped"}") }
    }

    includeImplementation(versionedCatalog["placeholders"])
    "include"(versionedCatalog["cats"])
    "implementation"(versionedCatalog["cats"])
    maybeModApi(versionedCatalog["fabric.loader"])
    maybeModApi(versionedCatalog["repo.lib"])
    maybeModApi(versionedCatalog["fabric.language.kotlin"])
    maybeModApi(versionedCatalog["fabric.api"])
    maybeModApi(versionedCatalog["hypixelapi"])
    "compileOnly"(versionedCatalog["meowdding.ktmodules"])
    "compileOnly"(versionedCatalog["meowdding.ktcodecs"])
    "ksp"(versionedCatalog["meowdding.ktmodules"])
    "ksp"(versionedCatalog["meowdding.ktcodecs"])

    // Jade compat
    maybeModCompileOnly(versionedCatalog["jade"])
    maybeModLocalRuntime(versionedCatalog["jade"])

    // Packed Packs compat
    maybeModCompileOnly(versionedCatalog["packed_packs"])
    maybeModLocalRuntime(versionedCatalog["packed_packs"])

    maybeModRuntimeOnly(versionedCatalog["devauth"])
}

fun DependencyHandlerScope.includeImplementation(dep: Any) {
    "include"(dep)
    maybeModImplementation(dep)
}

autoMixins {
    mixinPackage = "me.owdding.catharsis.mixins"
    projectName = "catharsis"
    mixinExtrasVersion = "0.5.0"
}
