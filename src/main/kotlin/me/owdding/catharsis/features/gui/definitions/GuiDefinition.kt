package me.owdding.catharsis.features.gui.definitions

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefConditions
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.nonPartialFieldOf
import me.owdding.catharsis.utils.codecs.nonPartialListOf
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

data class GuiDefinition(
    val priority: Int = 0,
    val target: GuiDefinitionCondition,
    val layout: List<GuiSlotDefinition>,
) {

    fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return target.matches(screen)
    }

    companion object {
        val CODEC: Codec<GuiDefinition> = RecordCodecBuilder.create { it.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(GuiDefinition::priority),
            GuiDefConditions.CODEC.fieldOf("target").forGetter(GuiDefinition::target),
            CatharsisCodecs.getCodec<GuiSlotDefinition>().listOf().fieldOf("layout").forGetter(GuiDefinition::layout),
        ).apply(it, ::GuiDefinition) }

        val STRICT_CODEC: Codec<GuiDefinition> = RecordCodecBuilder.create { it.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(GuiDefinition::priority),
            GuiDefConditions.CODEC.nonPartialFieldOf("target").forGetter(GuiDefinition::target),
            CatharsisCodecs.getCodec<GuiSlotDefinition>().nonPartialListOf().fieldOf("layout").forGetter(GuiDefinition::layout),
        ).apply(it, ::GuiDefinition) }
    }
}

