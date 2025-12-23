package me.owdding.catharsis.repo


import com.google.common.hash.Hashing
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

private const val REMOTE_URL = "catharsis-repo.pages.dev"

object CatharsisRemoteRepo : CatharsisLogger by Catharsis.featureLogger() {

    private val gson: Gson = GsonBuilder().create()
    val cacheDirectory: Path= McClient.config.resolveSibling("catharsis-repo-cache")
    private var version: String? = null
    private var isInitialized = false
    var forceBackupRepo: Boolean = false
    const val REPO_BRANCH_PROPERTY = "repo_branch"

    fun initialize(version: String, callback: () -> Unit) {
        if (isInitialized) return
        CatharsisRemoteRepo.version = version.takeUnless { it == "stable" }
        val httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()

        val currentRepoHash = cacheDirectory.resolve("index.json.sha").takeIf { it.exists() }?.readText(Charsets.UTF_8)
        val remoteRepoHash = httpClient.get("index.json.sha")

        if (remoteRepoHash == null || forceBackupRepo) {
            loadBackupRepo()
        } else if (currentRepoHash != remoteRepoHash) {
            val oldFiles = listFilesInDirectory().toMap().toMutableMap()
            httpClient.downloadOrUpdate(remoteRepoHash, oldFiles)
            if (oldFiles.isNotEmpty()) {
                warn("Deleting ${oldFiles.size} outdated files!")
                oldFiles.forEach { (_, value) ->
                    debug("Deleting ${value.absolutePathString()}")
                }
            }
        }

        isInitialized = true
        callback()
    }

    fun uninitialize() {
        isInitialized = false
    }

    fun invalidate() {
        isInitialized = false
        cacheDirectory.deleteRecursively()
    }

    fun isInitialized() = isInitialized
    fun getFileContent(file: String) = cacheDirectory.resolve(file).takeIf { it.exists() }?.readText(Charsets.UTF_8) ?: run {
        Catharsis.warn("Requested unknown file $file from remote repo!")
        null
    }
    fun getFileContentAsJson(file: String) = getFileContent("${file.removeSuffix(".json")}.json")?.let { gson.fromJson(it, JsonElement::class.java) }

    fun listFilesInDirectory(directory: String): List<Pair<String, Path>> {
        val list = mutableListOf<Pair<String, Path>>()

        val directory = cacheDirectory.resolve(directory)
        directory.walk().forEach {
            val relative = directory.relativize(it)
            list.add(relative.toString().lowercase().replace("\\", "/").filter(Identifiers::isAllowedInIdentifier) to it)
        }

        return list
    }

    fun listFilesInDirectory(): List<Pair<String, Path>> {
        val list = mutableListOf<Pair<String, Path>>()

        val directory = cacheDirectory
        directory.walk().forEach {
            val relative = directory.relativize(it)
            list.add(relative.toString().lowercase().replace("\\", "/") to it)
        }

        return list
    }

    private fun HttpClient.downloadOrUpdate(remoteHash: String, oldFiles: MutableMap<String, Path>) {
        cacheDirectory.createDirectories()
        val currentIndex = getFileContentAsJson("index.json")?.asJsonObject ?: JsonObject()
        val remoteIndex = getJsonObject("index.json") ?: run {
            Catharsis.warn("Failed to load repo data, falling back to backup repo!")
            loadBackupRepo()
            return
        }

        val cache = mutableMapOf<String, String>()
        oldFiles.remove("index.json")
        oldFiles.remove("index.json.sha")
        remoteIndex.entrySet().forEach { (key, hash) ->

            val expectedHash = hash.asString

            oldFiles.remove(key)
            if (currentIndex.has(key)) {
                val storedHash = currentIndex[key].asString

                val path = cacheDirectory.resolve(key)
                val realHash = if (path.exists()) Hashing.sha256().hashBytes(path.toFile().readBytes()).toString() else null
                if (storedHash == expectedHash && realHash == expectedHash) return@forEach
            }
            cacheDirectory.resolve(key)
            cache[key] = get(key) ?: run {
                Catharsis.warn("Failed to load repo data, falling back to backup repo!")
                loadBackupRepo()
                return
            }
        }

        cache["index.json"] = remoteIndex.toString()
        cache["index.json.sha"] = remoteHash

        cache.forEach { (key, value) ->
            val key = cacheDirectory.resolve(key).normalize()
            if (!key.startsWith(cacheDirectory)) {
                Catharsis.warn("Bad key found! Skipping $key!")
                return@forEach
            }
            key.createParentDirectories()
            key.writeText(value, Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        }
    }

    private fun String.toJsonObject(): JsonObject = gson.fromJson(this, JsonObject::class.java)

    private fun getFromBackup(path: String): ByteArray? = CatharsisRemoteRepo::class.java.getResourceAsStream("/repo/$path")?.readAllBytes()

    private fun Path.deleteRecursively() {
        if (this.isDirectory()) {
            this.listDirectoryEntries().forEach {
                it.deleteRecursively()
            }
        }
        this.deleteIfExists()
    }

    // TODO: fix backup repo not working
    private fun loadBackupRepo() {
        cacheDirectory.deleteRecursively()
        cacheDirectory.createDirectories()
        fun error(): Nothing = kotlin.error("Failed to restore backup repo!")
        val indexData = getFromBackup("index.json") ?: error()
        val index = indexData.toString(Charsets.UTF_8).toJsonObject()

        index.entrySet().forEach { (key) ->
            cacheDirectory.resolve(key).createParentDirectories().writeBytes(getFromBackup(key) ?: error(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        }
        cacheDirectory.resolve("index.json").writeBytes(indexData)
        cacheDirectory.resolve("index.json.sha").writeBytes(getFromBackup("index.json.sha") ?: error())
    }

    private fun HttpClient.getJsonObject(path: String) = get(path)?.let { gson.fromJson(it, JsonObject::class.java) }
    private fun HttpClient.get(path: String): String? = runCatching {
        send(
            HttpRequest.newBuilder(URI.create("https://" + (version?.let { "$it." } ?: "") + REMOTE_URL).resolve(path))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build(),
            HttpResponse.BodyHandlers.ofString(Charsets.UTF_8),
        ).let { it.body().takeUnless { _ -> it.statusCode() != 200 } }
    }.getOrNull()

}

