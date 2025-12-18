@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    idea
    id("fabric-loom")
    kotlin("jvm") version "2.2.20"
    alias(libs.plugins.ksp)
    `versioned-catalogues`
}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven("https://maven.teamresourceful.com/repository/maven-public/", "tech.thatgravyboat", "me.owdding")
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    mavenCentral()
}

dependencies {
    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })

    api(libs.skyblockapi) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }
    include(libs.skyblockapi) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}-remapped") }
    }

    includeImplementation(versionedCatalog["placeholders"])
    modImplementation(libs.fabric.loader)
    modImplementation(libs.repo.lib)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(versionedCatalog["fabric.api"])
    modImplementation(libs.hypixelapi)
    compileOnly(libs.meowdding.ktmodules)
    compileOnly(libs.meowdding.ktcodecs)
    ksp(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktcodecs)

    modRuntimeOnly(libs.devauth)
}

fun DependencyHandler.includeImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

val accessWidenerFile = rootProject.file("src/catharsis.accesswidener")
val mcVersion = stonecutter.current.version.replace(".", "")
loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=" + '"' + rootProject.projectDir.resolve("run/${mcVersion}Mods").absolutePath + '"')
    }

    accessWidenerPath.set(accessWidenerFile)
}

ksp {
    arg("meowdding.project_name", "catharsis")
    arg("meowdding.package", "me.owdding.catharsis.generated")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
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
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.add("-Xnullability-annotations=@org.jspecify.annotations:warn")
    compilerOptions.freeCompilerArgs.add("-Xwhen-guards")
}

tasks.processResources {
    val replacements = mapOf(
        "version" to version,
        "minecraft_start" to versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft"),
        "minecraft_end" to versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft"),
        "fabric_lang_kotlin" to libs.versions.fabric.language.kotlin.get(),
        "sbapi" to libs.versions.skyblockapi.get(),
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
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
    })
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }
