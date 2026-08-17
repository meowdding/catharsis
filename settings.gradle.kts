pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.7"
}
rootProject.name = "catharsis"

val versions = listOf("26.3", "26.2", "26.1") // Also manually update the versionCatalogs at the bottom for depentabot

stonecutter {
    create(rootProject) {
        versions.forEach {
            version(it)
        }
        vcsVersion = versions.first()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs263") {
            from(files("gradle/26_3.versions.toml"))
        }
        create("libs262") {
            from(files("gradle/26_2.versions.toml"))
        }
        create("libs261") {
            from(files("gradle/26_1.versions.toml"))
        }
    }
}

fun includeProject(name: String, fileName: String = "$name.gradle.kts") {
    include(name)
    project(":$name").apply {
        buildFileName = "../$fileName"
    }
}

includeProject("repo")
includeProject("scripts")
