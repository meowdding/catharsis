package me.owdding.catharsis.features.dev

import me.owdding.catharsis.features.gui.definitions.GuiDefinitions
import me.owdding.catharsis.utils.debugToggle
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Module
object DebugGui {

    private val debug by debugToggle("gui", "Show debug information for GUI definitions")

    @Subscription
    fun onScreenRender(event: RenderScreenForegroundEvent) {
        if (!debug) return
        val container = event.screen as? AbstractContainerScreen<*> ?: return

        val lines = mutableListOf<Component>()

        val guis = GuiDefinitions.getGuis()
        val slot = container.getHoveredSlot()?.let { GuiDefinitions.getSlot(it.index) }

        lines.add(Text.of("Hovered Slot:", TextColor.BLUE))
        lines.add(Text.of(" - ${slot ?: "None"}"))
        lines.add(Text.of())
        lines.add(Text.of("Applicable GUIs:", TextColor.BLUE))
        guis.forEachIndexed { index, gui ->
            lines.add(Text.of(" - $gui", if (index == 0) TextColor.YELLOW else TextColor.WHITE))
        }

        val height = lines.size * 10
        val startY = (event.screen.height / 2) - (height / 2)
        lines.forEachIndexed { index, line ->
            event.graphics.drawString(line, 5, startY + index * 10)
        }
    }
}
