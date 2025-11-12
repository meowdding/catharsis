package me.owdding.catharsis.features.pack.config

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.CommonComponents
import net.minecraft.util.CommonColors

class DividerElement(x: Int = 0, y: Int = 0, width: Int = 0, height: Int = 0) : AbstractWidget(x, y, width, height, CommonComponents.EMPTY) {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, CommonColors.LIGHT_GRAY)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }
}
