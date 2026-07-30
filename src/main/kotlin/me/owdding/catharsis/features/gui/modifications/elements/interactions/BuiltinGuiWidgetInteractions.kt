package me.owdding.catharsis.features.gui.modifications.elements.interactions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.screens.ConfirmLinkScreen
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.ContainerInput
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import java.net.URI

@GenerateCodec
data class GuiLinkWidgetInteraction(
    val url: URI,
) : GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiLinkWidgetInteraction>()
    override fun click(button: Int) {
        ConfirmLinkScreen.confirmLinkNow(McScreen.self, url)
    }
}

@GenerateCodec
data class GuiSlotClickWidgetInteraction(
    val slot: Int,
    val alwaysMiddleClick: Boolean = true,
) : GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiSlotClickWidgetInteraction>()
    override fun click(button: Int) {
        val menu = McScreen.asMenu?.menu ?: return
        val slotId = menu.getSlot(slot).takeIf { it != null && it.index == slot }?.index ?: return
        val player = McPlayer.self ?: return

        if (alwaysMiddleClick) {
            // This simulates a "pick block" action on the slot, this is ONLY possible when using the pick key as a keyboard bind, not by mouse.
            // Every update we need to check if this is still possible as to not send invalid packets.
            // TODO maybe add ability to remotely disable this if hypixel does a patch that breaks it, but doubtful since its possible in vanilla.
            McClient.self.gameMode?.handleContainerInput(menu.containerId, slotId, 0, ContainerInput.CLONE, player)
        } else {
            // allow left/right/middle click + with shift modifier
            val input = if (McScreen.isShiftDown) ContainerInput.QUICK_MOVE else ContainerInput.PICKUP
            McClient.self.gameMode?.handleContainerInput(menu.containerId, slotId, button, input, player)
        }
    }
}

@GenerateCodec
data class GuiSlotIdClickWidgetInteraction(
    val slot: Identifier,
    val alwaysMiddleClick: Boolean = true,
) : GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiSlotIdClickWidgetInteraction>()

    fun getSlot() = McScreen.asMenu?.menu?.slots?.firstOrNull { GuiDefinitions.getSlot(it.index) == slot }

    override fun click(button: Int) {
        val slot = getSlot() ?: return
        GuiSlotClickWidgetInteraction(slot.index, alwaysMiddleClick).click(button)
    }
}


@GenerateCodec
data class GuiCommandWidgetInteraction(
    val command: String,
) : GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiCommandWidgetInteraction>()
    override fun click(button: Int) {
        if (CommandWhiteList.isWhitelisted(command)) {
            McClient.sendCommand(command)
        } else {
            Catharsis.warn("Command '$command' is not whitelisted")
        }
    }
}

data object GuiNoOpWidgetInteraction : GuiWidgetInteraction {
    override val codec: MapCodec<out GuiWidgetInteraction> get() = MapCodec.unit { GuiNoOpWidgetInteraction }
    override fun click(button: Int) = Unit
}
