package me.owdding.catharsis.utils

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writer

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
internal data object CatharsisDevUtils : DevUtils() {

    override val path: Path = System.getProperty("catharsis.property_path")?.let { Path(it) } ?: McClient.config.resolve("catharsis.properties")
    override val commandName: String = "catharsis dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()

    init {
        loadFromProperties(path)
    }

    override val supportsChangingDefault: Boolean = true
    override val namespace: String = "catharsis"

    fun getString(key: String, default: String): String = getString(key) ?: default
    fun getString(key: String): String? = properties.getProperty(key)
    fun setString(key: String, value: String?) {
        if (value == null) this.properties.remove(key)
        else this.properties.setProperty(key, value)
    }

    fun getInt(key: String, default: Int = 0): Int {
        return getString(key).parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean = getString(key) == "true"

    fun saveProperties() {
        path.createParentDirectories()
        path.writer().use {
            properties.store(it, null)
        }
    }
}
