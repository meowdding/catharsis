package me.owdding.catharsis.features.gui.definitions.conditions

import me.owdding.catharsis.features.gui.definitions.slots.SlotCondition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@GenerateCodec
data class GuiDefAllCondition(val conditions: List<GuiDefCondition>): GuiDefCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefAllCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.all { it.matches(screen) }
}

@GenerateCodec
data class GuiDefAnyCondition(val conditions: List<GuiDefCondition>): GuiDefCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefAnyCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.any { it.matches(screen) }
}

@GenerateCodec
data class GuiDefSlotCondition(val index: Int, val conditions: List<SlotCondition>): GuiDefCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefSlotCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        val slot = screen.menu.getSlot(this.index) ?: return false
        return this.conditions.all { it.matches(slot) }
    }
}

@GenerateCodec
data class GuiDefTitleCondition(val title: Regex): GuiDefCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefTitleCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return this.title.matches(screen.title.stripped)
    }
}

// TODO see comment in IncludedCodecs
//@GenerateCodec
//data class GuiTypeCondition(val type: MenuType<*>): GuiCondition {
//    override val codec = CatharsisCodecs.getMapCodec<GuiSlotCondition>()
//    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
//        return this.type == screen.menu.type
//    }
//}
