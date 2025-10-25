package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiWidgetInteraction
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.input.MouseButtonEvent

enum class GuiElementRenderLayer {
    BACKGROUND,
    FOREGROUND,
}

interface GuiElement {

    val codec: MapCodec<out GuiElement>
    val layer: GuiElementRenderLayer get() = GuiElementRenderLayer.FOREGROUND

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle)
}

interface GuiWidgetElement : GuiElement {

    val interaction: GuiWidgetInteraction
    override val codec: MapCodec<out GuiWidgetElement>

    fun isHovered(mouseX: Int, mouseY: Int, bounds: ScreenRectangle): Boolean
    fun onClick(event: MouseButtonEvent) = interaction.click(event)
}
