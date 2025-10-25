package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiWidgetInteraction
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation

@GenerateCodec
data class GuiButtonElement(
    val normal: ResourceLocation,
    val hovered: ResourceLocation,

    override val interaction: GuiWidgetInteraction,

    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
) : GuiWidgetElement {

    override val codec: MapCodec<GuiButtonElement> = CatharsisCodecs.getMapCodec<GuiButtonElement>()

    override fun isHovered(mouseX: Int, mouseY: Int, bounds: ScreenRectangle): Boolean {
        val newX = bounds.left() + x
        val newY = bounds.top() + y
        return mouseX >= newX && mouseX <= newX + width && mouseY >= newY && mouseY <= newY + height
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val sprite = if (isHovered(mouseX, mouseY, bounds)) hovered else normal
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, bounds.left() + x, bounds.top() + y, width, height)
    }

}
