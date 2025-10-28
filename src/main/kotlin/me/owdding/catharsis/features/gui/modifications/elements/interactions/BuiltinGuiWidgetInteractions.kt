package me.owdding.catharsis.features.gui.modifications.elements.interactions

import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.screens.ConfirmLinkScreen
import net.minecraft.world.inventory.ClickType
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import java.net.URI

@GenerateCodec
data class GuiLinkWidgetInteraction(
    val url: URI
): GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiLinkWidgetInteraction>()
    override fun click(button: Int) {
        ConfirmLinkScreen.confirmLinkNow(McScreen.self, url)
    }
}

@GenerateCodec
data class GuiSlotClickWidgetInteraction(
    val slot: Int
): GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiSlotClickWidgetInteraction>()
    override fun click(button: Int) {
        val menu = McScreen.asMenu?.menu ?: return
        val slotId = menu.getSlot(slot).takeIf { it != null && it.index == slot }?.index ?: return
        val player = McPlayer.self ?: return

        // This simulates a "pick block" action on the slot, this is ONLY possible when using the pick key as a keyboard bind, not by mouse.
        // Every update we need to check if this is still possible as to not send invalid packets.
        // TODO maybe add ability to remotely disable this if hypixel does a patch that breaks it, but doubtful since its possible in vanilla.
        McClient.self.gameMode?.handleInventoryMouseClick(menu.containerId, slotId, 0, ClickType.CLONE, player)
    }
}


@GenerateCodec
data class GuiCommandWidgetInteraction(
    val command: String
): GuiWidgetInteraction {
    override val codec = CatharsisCodecs.getMapCodec<GuiCommandWidgetInteraction>()
    override fun click(button: Int) {
        McClient.sendCommand(command)
    }
}
