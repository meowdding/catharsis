@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    id("net.fabricmc.fabric-loom")
    idea
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("versioned-catalogues")
    id("me.owdding.auto-mixins")
}

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
    scopedMaven("https://maven.operationpotato.com/snapshots", "com.operationpotato")
    scopedMaven("https://maven.operationpotato.com/releases", "com.operationpotato")
    mavenCentral()
    mavenLocal()
}

val accessWidenerFile = rootProject.file("src/katharsis.accesswidener")
val mcVersion = stonecutter.current.version.replace(".", "")

loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=${rootProject.projectDir.resolve("run/${mcVersion}Mods").absolutePath}")
    }

    accessWidenerPath.set(accessWidenerFile)
}

ksp {
    arg("meowdding.project_name", "katharsis")
    arg("meowdding.package", "me.owdding.katharsis.generated")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

val archiveName = "Katharsis"

base {
    archivesName.set("$archiveName-${archivesName.get()}")
}

tasks.build {
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

    filesMatching("katharsis.json") {
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


dependencies {
    minecraft(versionedCatalog["minecraft"])

    api(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }
    include(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }

    includeImplementation(versionedCatalog["placeholders"])
    include(versionedCatalog["cats"])
    implementation(versionedCatalog["cats"])
    api(versionedCatalog["fabric.loader"])
    api(versionedCatalog["repo.lib"])
    api(versionedCatalog["fabric.language.kotlin"])
    api(versionedCatalog["fabric.api"])
    api(versionedCatalog["hypixelapi"])
    compileOnly(versionedCatalog["meowdding.ktmodules"])
    compileOnly(versionedCatalog["meowdding.ktcodecs"])
    ksp(versionedCatalog["meowdding.ktmodules"])
    ksp(versionedCatalog["meowdding.ktcodecs"])

    // Jade compat
    compileOnly(versionedCatalog["jade"])
    localRuntime(versionedCatalog["jade"])

    // Packed Packs compat
    compileOnly(versionedCatalog["packed_packs"])
    localRuntime(versionedCatalog["packed_packs"])

    // SkyBlockItemList compat
    versionedCatalog.getOrNull("skyblock-item-list")?.let {
        compileOnly(it)
        localRuntime(it)
    }

    runtimeOnly(versionedCatalog["devauth"])
}

fun DependencyHandlerScope.includeImplementation(dep: Any) {
    include(dep)
    implementation(dep)
}

autoMixins {
    mixinPackage = "me.owdding.katharsis.mixins"
    projectName = "katharsis"
    mixinExtrasVersion = "0.5.0"
}
