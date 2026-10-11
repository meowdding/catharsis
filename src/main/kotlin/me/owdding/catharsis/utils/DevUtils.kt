package me.owdding.catharsis.utils

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.DevUtils
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
internal object CatharsisDevUtils : DevUtils() {

    override val path: Path = System.getProperty("catharsis.property_path")?.let { Path(it) } ?: McClient.config.resolve("catharsis.properties")
    override val commandName: String = "catharsis dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()

    init {
        loadFromProperties(path)
    }

    override val supportsChangingDefault: Boolean = true
    override val namespace: String = "catharsis"

    fun getString(key: String, default: String): String = getString(key) ?: default
    fun getString(key: String): String? = propertiesInstance.getProperty(key)
    fun setString(key: String, value: String?) {
        if (value == null) this.propertiesInstance.remove(key)
        else this.propertiesInstance.setProperty(key, value)
    }

    fun getInt(key: String, default: Int = 0): Int {
        return getString(key).parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean = getString(key) == "true"

    fun saveProperties() {
        path.createParentDirectories()
        path.writer().use {
            propertiesInstance.store(it, null)
        }
    }

    @Subscription
    private fun commandRegister(event: RegisterCommandsEvent) = super.onCommandRegister(event)
}
