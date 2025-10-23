package me.owdding.catharsis.features.gui.definitions.conditions

import me.owdding.catharsis.features.gui.definitions.slots.SlotCondition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@GenerateCodec
data class GuiAllCondition(val conditions: List<GuiCondition>): GuiCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiAllCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.all { it.matches(screen) }
}

@GenerateCodec
data class GuiAnyCondition(val conditions: List<GuiCondition>): GuiCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiAnyCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.any { it.matches(screen) }
}

@GenerateCodec
data class GuiSlotCondition(val index: Int, val conditions: List<SlotCondition>): GuiCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiSlotCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        val slot = screen.menu.getSlot(this.index) ?: return false
        return this.conditions.all { it.matches(slot) }
    }
}

@GenerateCodec
data class GuiTitleCondition(val title: Regex): GuiCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiSlotCondition>()
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
