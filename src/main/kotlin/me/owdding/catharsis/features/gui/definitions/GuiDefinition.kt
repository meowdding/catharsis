package me.owdding.catharsis.features.gui.definitions

import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

@GenerateCodec
data class GuiDefinition(
    val target: GuiDefinitionCondition,
    val layout: List<GuiSlotDefinition>,
) {

    fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return target.matches(screen)
    }
}

