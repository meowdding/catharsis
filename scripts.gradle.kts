@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URLEncoder
import kotlin.collections.emptyList
import kotlin.text.replace

plugins {
    idea
    kotlin("jvm")
}

layout.buildDirectory = rootProject.layout.buildDirectory.map { it.dir("scripts-build") }

sourceSets {
    main {
        kotlin {
            srcDirs(listOf(layout.projectDirectory))
        }
        resources {
            setSrcDirs(emptyList<Any>())
        }
        java {
            setSrcDirs(emptyList<Any>())
        }
    }
}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven("https://maven.teamresourceful.com/repository/maven-public/", "tech.thatgravyboat", "me.owdding")
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    scopedMaven("https://maven.fabricmc.net/", "net.fabricmc")
    scopedMaven("https://raw.githubusercontent.com/fishstiz/maven/m2", "io.github.fishstiz")
    maven("https://libraries.minecraft.net")
    maven(file(rootProject.projectDir.resolve(".gradle/loom-cache/minecraftMaven")))
    maven(file(rootProject.projectDir.resolve(".gradle/loom-cache/remapped_mods")))
    mavenCentral()
    mavenLocal()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

kotlin {
    jvmToolchain(25)
}

val latest = rootProject.properties["latest"]!!.toString()

evaluationDependsOn(":$latest")

tasks.withType<KotlinCompile> {
    compilerOptions.freeCompilerArgs.add("-Xallow-any-scripts-in-source-roots")
}

dependencies {
    runtimeOnly(project(":$latest", configuration = "fat"))
    compileOnly(project(":$latest", configuration = "fat"))
    implementation(project(":$latest", configuration = "unobfuscatedBuild"))
}
