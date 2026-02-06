package me.owdding.catharsis.features.gui.definitions.conditions

import me.owdding.catharsis.features.gui.definitions.slots.SlotCondition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.MenuType
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@GenerateCodec
data class GuiDefinitionAllCondition(
    val conditions: List<GuiDefinitionCondition>
) : GuiDefinitionCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionAllCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.all { it.matches(screen) }
}

@GenerateCodec
data class GuiDefinitionAnyCondition(
    val conditions: List<GuiDefinitionCondition>
) : GuiDefinitionCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionAnyCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = this.conditions.any { it.matches(screen) }
}

@GenerateCodec
data class GuiDefinitionSlotCondition(
    val index: Int,
    val condition: SlotCondition
) : GuiDefinitionCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionSlotCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        val slot = screen.menu.getSlot(this.index) ?: return false
        return this.condition.matches(slot.index, slot.item)
    }
}

@GenerateCodec
data class GuiDefinitionTitleCondition(val title: Regex) : GuiDefinitionCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionTitleCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return this.title.matches(screen.title.stripped)
    }
}

@GenerateCodec
data class GuiDefinitionTypeCondition(val type: MenuType<*>): GuiDefinitionCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionTypeCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return this.type == screen.menu.type
    }
}

@GenerateCodec
data class GuiDefinitionIslandCondition(val islands: Set<SkyBlockIsland>) : GuiDefinitionCondition {
    override val codec = CatharsisCodecs.getMapCodec<GuiDefinitionIslandCondition>()
    override fun matches(screen: AbstractContainerScreen<*>): Boolean = SkyBlockIsland.inAnyIsland(islands)
}
