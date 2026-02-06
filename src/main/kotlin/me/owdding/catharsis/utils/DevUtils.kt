package me.owdding.catharsis.utils

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.outputStream
import kotlin.io.path.reader

internal fun debugToggle(path: String, description: String = path): DebugToggle {
    return DebugToggle(Catharsis.id(path), description, CatharsisDevUtils)
}

internal fun <T : Any> debugSelect(
    path: String,
    description: String = path,
    initialState: T?,
    states: List<T>,
    toString: (T) -> String = { it.toString() },
): DebugSelect<T> {
    return DebugSelect(Catharsis.id(path), description, CatharsisDevUtils, initialState, toString, states)
}

@Module
internal object CatharsisDevUtils : DevUtils() {

    private val path = System.getProperty("catharsis.property_path")?.let { Path(it) } ?: McClient.config.resolve("catharsis.properties")
    override val commandName: String = "catharsis dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    val properties: MutableMap<String, String> = loadFromProperties().toMutableMap()

    fun getString(key: String, default: String): String = properties[key] ?: default
    fun getString(key: String): String? = properties[key]

    fun getInt(key: String, default: Int = 0): Int {
        return properties[key].parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean {
        return properties[key] == "true"
    }

    private fun loadProperties(): Properties {
        val properties = Properties()
        if (path.exists()) {
            path.reader(Charsets.UTF_8).use {
                properties.load(it)
            }
        }
        return properties
    }

    fun saveProperties() {
        val properties = Properties()
        properties.putAll(this.properties)
        properties.store(this.path.outputStream(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), null)
    }

    private fun loadFromProperties(): Map<String, String> {
        val properties = loadProperties()
        val map = mutableMapOf<String, String>()
        properties.forEach { (key, value) ->
            Identifiers.parseWithSeparator(key.toString(), '@')?.let {
                if (value.toString() == "true") {
                    states[it] = true
                }
            }
            map[key.toString()] = value.toString()
        }
        return map
    }

    @Subscription
    fun commandRegister(event: RegisterCommandsEvent) = super.onCommandRegister(event)
}
