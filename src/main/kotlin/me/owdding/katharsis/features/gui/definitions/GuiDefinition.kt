package me.owdding.katharsis.features.gui.definitions

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.katharsis.features.gui.definitions.conditions.GuiDefConditions
import me.owdding.katharsis.features.gui.definitions.conditions.GuiDefinitionCondition
import me.owdding.katharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.SavableData
import me.owdding.katharsis.utils.codecs.nonPartialFieldOf
import me.owdding.katharsis.utils.codecs.nonPartialListOf
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.Slot

data class GuiDefinition(
    val priority: Int = 0,
    val target: GuiDefinitionCondition,
    val layout: List<GuiSlotDefinition>,
) : SavableData<GuiDefinition> {
    override val codec: Codec<GuiDefinition> get() = CODEC
    override fun toFileName(identifier: Identifier): Identifier = GuiDefinitions.uiDefinitionConverter.idToFile(identifier)

    fun matches(slots: List<Slot>, screen: AbstractContainerScreen<*>): Boolean {
        return target.matches(slots, screen)
    }

    companion object {
        val CODEC: Codec<GuiDefinition> = RecordCodecBuilder.create { it.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(GuiDefinition::priority),
            GuiDefConditions.CODEC.fieldOf("target").forGetter(GuiDefinition::target),
            KatharsisCodecs.getCodec<GuiSlotDefinition>().listOf().fieldOf("layout").forGetter(GuiDefinition::layout),
        ).apply(it, ::GuiDefinition) }

        val STRICT_CODEC: Codec<GuiDefinition> = RecordCodecBuilder.create { it.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(GuiDefinition::priority),
            GuiDefConditions.CODEC.nonPartialFieldOf("target").forGetter(GuiDefinition::target),
            KatharsisCodecs.getCodec<GuiSlotDefinition>().nonPartialListOf().fieldOf("layout").forGetter(GuiDefinition::layout),
        ).apply(it, ::GuiDefinition) }
    }
}

