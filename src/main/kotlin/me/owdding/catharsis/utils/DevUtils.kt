package me.owdding.catharsis.utils

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.notExists
import kotlin.io.path.reader

internal fun debugToggle(path: String, description: String = path): DebugToggle {
    return DebugToggle(Catharsis.id(path), description, CatharsisDevUtils)
}
@Module
internal object CatharsisDevUtils : DevUtils() {
    override val commandName: String = "catharsis toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    val properties: Map<String, String> = loadFromProperties()

    fun getInt(key: String, default: Int = 0): Int {
        return properties[key].parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean {
        return properties[key] == "true"
    }

    private fun loadFromProperties(): Map<String, String> {
        val properties = Properties()
        val path = System.getProperty("sbapi.property_path")?.let { Path(it) } ?: McClient.config.resolve("catharsis.properties")
        if (path.notExists()) return emptyMap()
        path.reader(Charsets.UTF_8).use {
            properties.load(it)
        }
        val map = mutableMapOf<String, String>()
        properties.forEach { (key, value) ->
            ResourceLocation.tryBySeparator(key.toString(), '@')?.let {
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
