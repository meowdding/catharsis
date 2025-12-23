package me.owdding.catharsis.features.gui.definitions.slots

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class GuiSlotDefinition(
    val id: Identifier,
    val target: SlotCondition,
) {

    fun matches(slot: Int, stack: ItemStack): Boolean {
        return target.matches(slot, stack)
    }
}
