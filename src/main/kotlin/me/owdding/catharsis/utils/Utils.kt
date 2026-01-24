package me.owdding.catharsis.utils

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object Utils {

    fun resourceLocationWithDifferentFallbackNamespace(location: String, separator: Char, namespace: String): Identifier {
        val i = location.indexOf(separator)
        return if (i >= 0) {
            val string = location.substring(i + 1)

            if (i != 0) {
                Identifier.fromNamespaceAndPath(location.take(i), string)
            } else {
                Identifier.fromNamespaceAndPath(namespace, string)
            }
        } else {
            Identifier.fromNamespaceAndPath(namespace, location)
        }
    }

    private val pattern = Regex("(?i)\\u00A7.")
    val componentCache: LoadingCache<Component, String> = CacheBuilder.newBuilder()
        .weakKeys()
        .expireAfterWrite(1.minutes.toJavaDuration())
        .build<Component, String>(
            object : CacheLoader<Component, String>() {
                override fun load(key: Component): String {
                    return key.stripped.replace(pattern, "")
                }
            }
        )

    val Component.fastStripped: String
        get() = componentCache.get(this)
}
