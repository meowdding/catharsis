package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.gui.modifications.elements.conditions.GuiElementCondition
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiWidgetInteraction
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

@GenerateCodec
data class GuiButtonElement(
    val normal: Identifier,
    val hovered: Identifier = normal,

    override val interaction: GuiWidgetInteraction,
    @Compact override val tooltip: List<GuiWidgetTooltip>?,

    val x: GuiElementPosition,
    val y: GuiElementPosition,
    val width: Int,
    val height: Int,
    override val condition: GuiElementCondition?,
) : GuiWidgetElement {

    override val codec: MapCodec<GuiButtonElement> = CatharsisCodecs.getMapCodec<GuiButtonElement>()

    override fun isHovered(mouseX: Int, mouseY: Int, bounds: ScreenRectangle): Boolean {
        val newX = x.calculate(bounds.left(), bounds.width())
        val newY = y.calculate(bounds.top(), bounds.height())
        return mouseX >= newX && mouseX <= newX + width && mouseY >= newY && mouseY <= newY + height
    }

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val sprite = if (isHovered(mouseX, mouseY, bounds)) hovered else normal
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x.calculate(bounds.left(), bounds.width()), y.calculate(bounds.top(), bounds.height()), width, height)
    }

}
