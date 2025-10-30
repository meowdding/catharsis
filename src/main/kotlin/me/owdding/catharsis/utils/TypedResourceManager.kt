package me.owdding.catharsis.utils

import com.google.gson.JsonElement
import me.owdding.catharsis.utils.extensions.readAsJson
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import kotlin.jvm.optionals.getOrNull

class TypedResourceManager(private val resources: ResourceManager) {

    private val cache = mutableMapOf<Pair<*, *>, Result<*>?>()

    private fun <T> load(id: ResourceLocation, parser: TypedResourceParser<T>): Result<T>? {
        return resources.getResource(id).getOrNull()?.runCatching {
            this.readAsJson<JsonElement>()
        }?.mapCatching {
            parser.parse(it).getOrThrow()
        }
    }

    fun <T> getOrLoad(id: ResourceLocation, parser: TypedResourceParser<T>): Result<T>? {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(id to parser) { load(id, parser) } as Result<T>?
    }
}

class TypedResourceParser<T>(
    val type: Class<T>,
    val parser: (JsonElement) -> Result<T>,
) {

    fun parse(element: JsonElement): Result<T> = parser(element)

    companion object {

        inline fun <reified T> ofResult(noinline parser: (JsonElement) -> Result<T>): TypedResourceParser<T> {
            return TypedResourceParser(T::class.java, parser)
        }

        inline fun <reified T> of(noinline parser: (JsonElement) -> T): TypedResourceParser<T> {
            return ofResult { runCatching { parser.invoke(it) } }
        }
    }
}
