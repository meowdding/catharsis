pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("dev.kikugie.stonecutter") version "0.9"
}
rootProject.name = "catharsis"

val versions = listOf("26.1", "1.21.11")

stonecutter {
    create(rootProject) {
        versions.forEach {
            version(it).buildscript = if (stonecutter.eval(it, "<=1.21.11")) "build.obf.gradle.kts" else "build.gradle.kts"
        }
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
