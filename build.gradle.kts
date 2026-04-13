@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.gradle.kotlin.dsl.processResources
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
    idea
    id("fabric-loom")
    kotlin("jvm")
    alias(libs.plugins.ksp)
    `versioned-catalogues`
}
