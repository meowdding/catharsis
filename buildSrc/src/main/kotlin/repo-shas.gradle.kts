@file:OptIn(ExperimentalPathApi::class)

import com.google.common.hash.Hashing
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlin.io.path.*

tasks.register("buildRepo") {
    val compactResources by tasks.getting
    dependsOn(compactResources)
    mustRunAfter(compactResources)
    inputs.files(compactResources.outputs.files)
    val targetDir = project.layout.buildDirectory.dir("repo")
    outputs.dir(targetDir)
    doFirst {
        val targetPath = targetDir.get().asFile.toPath()
        val compactingResourcesOutputDir = compactResources.outputs.files.first()
        val compactingResourcesOutputPath = compactingResourcesOutputDir.toPath()
        targetPath.deleteRecursively()
        targetPath.createDirectories()
        compactingResourcesOutputPath.copyToRecursively(targetPath, followLinks = false, overwrite = true)
        val map = mutableMapOf<String, String>()
        fileTree(compactingResourcesOutputDir) {
            include { true }
            forEach { file ->
                val relative = compactingResourcesOutputPath.relativize(file.toPath()).toString()
                val hash = Hashing.sha256().hashBytes(file.readBytes()).toString()
                map[relative] = hash
            }
        }

        val indexObject = JsonObject()
        map.entries.sortedBy { (key) -> key }.forEach { (key, value) ->
            indexObject.addProperty(key, value)
        }
        val index = Gson().toJson(indexObject).toByteArray(Charsets.UTF_8)
        targetPath.resolve("index.json").writeBytes(index)
        targetPath.resolve("index.json.sha").writeText(Hashing.sha256().hashBytes(index).toString())
    }
}

