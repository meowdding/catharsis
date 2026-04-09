package me.owdding.catharsis.utils.ui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen

open class Overlay : Screen(Component.empty()) {

    val background = McScreen.self

    override fun isPauseScreen(): Boolean = this.background?.isPauseScreen ?: false

    override fun added() {
        super.added()
        this.background?.clearFocus()
    }

    override fun repositionElements() {
        //? >= 1.21.11 {
        this.background?.resize(this.width, this.height)
        //?} else {
        /*this.background?.resize(this.minecraft!!, this.width, this.height)
        *///}
        super.repositionElements()
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.background?.renderWithTooltipAndSubtitles(graphics, -1, -1, partialTick)
        graphics.nextStratum()
    }

    override fun onClose() {
        McClient.setScreen(this.background)
    }
}
