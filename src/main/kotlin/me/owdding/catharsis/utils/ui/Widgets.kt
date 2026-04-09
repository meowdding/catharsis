package me.owdding.catharsis.utils.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.text.Text

abstract class BaseWidget(x: Int, y: Int, width: Int, height: Int) : AbstractWidget(x, y, width, height, Component.empty()) {

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }

}

abstract class BaseButtonWidget(x: Int, y: Int, width: Int, height: Int) : AbstractButton(x, y, width, height, Component.empty()) {

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }

    //? < 1.21.11 {
    /*override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderContents(graphics, mouseX, mouseY, partialTicks)
        if (this.isHovered()) {
            graphics.requestCursor(if (this.isActive) com.mojang.blaze3d.platform.cursor.CursorTypes.POINTING_HAND else com.mojang.blaze3d.platform.cursor.CursorTypes.NOT_ALLOWED)
        }
    }
    *///?}

    //? < 1.21.11 {
    /*abstract fun renderContents(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float)
    *///?}

    companion object {
        val SPRITES = WidgetSprites(
            Identifier.withDefaultNamespace("widget/button"),
            Identifier.withDefaultNamespace("widget/button_disabled"),
            Identifier.withDefaultNamespace("widget/button_highlighted")
        )
    }
}

class SelectedTextButton(x: Int, y: Int, width: Int, height: Int, text: Component, var selected: Boolean, val onPress: (SelectedTextButton) -> Unit) : BaseButtonWidget(x, y, width, height) {

    private val normalText: MutableComponent = text.copy()
    private val normalSelectedText: MutableComponent = Text.join("✔ ", text)
    private val hoveredText: MutableComponent = ComponentUtils.mergeStyles(normalText, Style.EMPTY.withUnderlined(true))
    private val hoveredSelectedText: MutableComponent = ComponentUtils.mergeStyles(normalSelectedText, Style.EMPTY.withUnderlined(true))

    override fun onPress(input: InputWithModifiers) {
        this.onPress(this)
    }

    override fun renderContents(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val component = when {
            this.selected && this.isHoveredOrFocused -> this.hoveredSelectedText
            this.selected -> this.normalSelectedText
            this.isHoveredOrFocused -> this.hoveredText
            else -> this.normalText
        }
        graphics.drawString(component, this.x, this.y)
    }
}
