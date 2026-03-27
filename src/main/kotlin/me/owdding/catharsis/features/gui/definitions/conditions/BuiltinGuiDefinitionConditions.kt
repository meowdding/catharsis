package me.owdding.catharsis.features.gui.definitions.conditions

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.gui.definitions.slots.SlotCondition
import me.owdding.catharsis.features.gui.matchers.RegexTextMatcher
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.CachedValue
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.minutes

@GenerateCodec
data class GuiDefinitionAllCondition(
    val conditions: List<GuiDefinitionCondition>,
) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionAllCondition>()
    override val cost: Int = this.conditions.sumOf { it.cost } + 1

    override fun optimize(): GuiDefinitionCondition = GuiDefinitionAllCondition(
        this.conditions.map(GuiDefinitionCondition::optimize).sortedBy(GuiDefinitionCondition::cost)
    )
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.all { it.matches(screen) }
}

@GenerateCodec
data class GuiDefinitionNotCondition(
    val condition: GuiDefinitionCondition,
) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionNotCondition>()
    override val cost: Int = this.condition.cost + 1

    override fun optimize(): GuiDefinitionCondition = GuiDefinitionNotCondition(this.condition.optimize())
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = !this.condition.matches(screen)
}

@GenerateCodec
data class GuiDefinitionAnyCondition(
    val conditions: List<GuiDefinitionCondition>,
) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionAnyCondition>()
    override val cost: Int = this.conditions.sumOf { it.cost } + 1

    override fun optimize(): GuiDefinitionCondition = GuiDefinitionAnyCondition(
        this.conditions.map(GuiDefinitionCondition::optimize).sortedBy(GuiDefinitionCondition::cost)
    )
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.any { it.matches(screen) }
}

@GenerateCodec
data class GuiDefinitionSlotCondition(
    val index: Int,
    val condition: SlotCondition,
) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionSlotCondition>()
    override val cost: Int = this.condition.cost + 1

    override fun optimize(): GuiDefinitionCondition = GuiDefinitionSlotCondition(this.index, this.condition.optimize())
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        val slot = screen.menu.getSlot(this.index) ?: return false
        return this.condition.matches(slot.index, slot.item)
    }
}

@GenerateCodec
data class GuiDefinitionTitleCondition(val title: Regex) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionTitleCondition>()
    override val cost: Int get() = RegexTextMatcher.COST

    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return this.title.matches(screen.title.stripped)
    }
}

@GenerateCodec
data class GuiDefinitionTypeCondition(val menu: GuiMenuType) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionTypeCondition>()

    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return this.menu.matches(screen)
    }
}

@GenerateCodec
data class GuiDefinitionIslandCondition(val islands: Set<SkyBlockIsland>) : GuiDefinitionCondition {

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionIslandCondition>()

    override fun matches(screen: AbstractContainerScreen<*>): Boolean = SkyBlockIsland.inAnyIsland(islands)
}

@GenerateCodec
data class GuiDefinitionExternalModConfigCondition(
    val mod: String?,
    @FieldName("file") val configFile: String,
    val path: String,
    val value: JsonElement,
) : GuiDefinitionCondition {
    val file: Path = McClient.config.resolve(configFile)

    val cache = CachedValue(timeToLive = 1.minutes) {
        if (!file.exists()) return@CachedValue false
        if (validJsons.any { file.endsWith(it) }) {
            Catharsis.error("Non json defined external mod config gui condition for path $configFile. This will not work.")
            return@CachedValue false
        }
        try {
            JsonParser.parseString(file.readText()).getPath(path) == value
        } catch (_: Exception) {
            false
        }
    }

    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionExternalModConfigCondition>()

    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        mod?.let {
            if (!FabricLoader.getInstance().isModLoaded(it)) return false
        }

        runCatching {
            if (cache.lastUpdated.toEpochMilliseconds() < file.getLastModifiedTime().toMillis()) {
                cache.invalidate()
            }
        }.getOrElse { cache.invalidate() }

        return cache.getValue()
    }

    companion object {
        private val validJsons = listOf("json", "jsonc", "json5")
    }
}
