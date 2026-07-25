package me.owdding.katharsis.features.pack.meta

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skyblockapi.utils.http.Http
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

data class PackUpdateInfo(
    val download: String?,
    val versions: Map<String, String> = emptyMap(),
)

object PackUpdateChecker {

    private val gson = Gson()
    private val cache = ConcurrentHashMap<String, Optional<PackUpdateInfo>>()

    fun requestUpdateInfo(url: String) {
        if (cache.containsKey(url)) return
        cache[url] = Optional.empty()

        CompletableFuture.runAsync {
            runBlocking {
                Http.get(url) {
                    if (this.isOk) {
                        cache[url] = Optional.of(this.asJson<PackUpdateInfo>(gson))
                    }
                }
            }
        }
    }

    fun getUpdateInfo(url: String): PackUpdateInfo? {
        return cache[url]?.getOrNull()
    }
}
