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
        // If cursor and only checking for slot index, fail the match
        if (slot == -1 && target is SlotIndexCondition) {
            return false
        }
        return target.matches(slot, stack)
    }
}
