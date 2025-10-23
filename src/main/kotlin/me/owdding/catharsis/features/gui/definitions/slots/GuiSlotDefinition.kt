package me.owdding.catharsis.features.gui.definitions.slots

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot

@GenerateCodec
data class GuiSlotDefinition(
    val id: ResourceLocation,
    val target: SlotCondition,
) {

    fun matches(slot: Slot): Boolean {
        return target.matches(slot)
    }
}
