pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("dev.kikugie.stonecutter") version "0.7.10"
}
rootProject.name = "catharsis"

val versions = listOf("1.21.11", "1.21.10")

stonecutter {
    create(rootProject) {
        versions(versions)
        vcsVersion = versions.first()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        versions.forEach {
            val name = it.replace(".", "")
            println("creating version catalogue libs$name")
            create("libs$name") {
                from(
                    files(
                        rootProject.projectDir.resolve("gradle/${it.replace(".", "_")}.versions.toml")
                    )
                )
            }
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
