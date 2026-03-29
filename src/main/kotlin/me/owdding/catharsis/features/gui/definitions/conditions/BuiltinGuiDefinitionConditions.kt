package me.owdding.catharsis.features.gui.definitions.conditions

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.gui.definitions.slots.SlotCondition
import me.owdding.catharsis.features.gui.matchers.RegexTextMatcher
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.CachedFile
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.nio.file.Path
import java.util.*
import kotlin.io.path.extension
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

class GuiDefinitionExternalModConfigCondition private constructor(
    val modId: String?,
    val file: Path,
    val path: String,
    val value: JsonElement,
) : GuiDefinitionCondition {

    private val cache by CachedFile(path = this.file, timeToLive = 1.minutes) { data ->
        try {
            JsonParser.parseString(data).getPath(path) == value
        } catch (_: Exception) {
            false
        }
    }

    override val cost: Int = 25
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionExternalModConfigCondition>()

    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        modId?.let {
            if (!FabricLoader.getInstance().isModLoaded(it)) return false
        }

        return cache
    }

    companion object {
        private val validJsons = listOf("json", "jsonc", "json5")
        private val normalizedConfig = McClient.config.normalize()
        private val configFileCodec: Codec<Path> = Codec.STRING.flatXmap(
            {
                val path = normalizedConfig.resolve(it).normalize()
                if (!path.startsWith(normalizedConfig) || path.extension !in validJsons) {
                    DataResult.error { "Invalid path: $it (must be within the config directory and has to be a json)" }
                } else {
                    DataResult.success(path)
                }
            },
            {
                if (it.startsWith(normalizedConfig)) {
                    DataResult.success(normalizedConfig.relativize(it).toString().replace('\\', '/'))
                } else {
                    DataResult.error { "Invalid path: $it (must be within the config directory, you cannot use ../ to go up a directory)" }
                }
            }
        )

        @IncludedCodec
        val codec: MapCodec<GuiDefinitionExternalModConfigCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("modId").forGetter { Optional.ofNullable(it.modId) },
                configFileCodec.fieldOf("file").forGetter(GuiDefinitionExternalModConfigCondition::file),
                Codec.STRING.fieldOf("path").forGetter(GuiDefinitionExternalModConfigCondition::path),
                ExtraCodecs.JSON.fieldOf("value").forGetter(GuiDefinitionExternalModConfigCondition::value),
            ).apply(instance) { modId, file, path, value ->
                GuiDefinitionExternalModConfigCondition(modId.orElse(null), file, path, value)
            }
        }
    }
}
