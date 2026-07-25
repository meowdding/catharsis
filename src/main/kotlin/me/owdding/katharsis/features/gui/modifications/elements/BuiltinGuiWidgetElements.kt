package me.owdding.katharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.gui.modifications.elements.conditions.GuiElementCondition
import me.owdding.katharsis.features.gui.modifications.elements.interactions.GuiNoOpWidgetInteraction
import me.owdding.katharsis.features.gui.modifications.elements.interactions.GuiWidgetInteraction
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore

@GenerateCodec
data class GuiButtonElement(
    val normal: Identifier,
    val hovered: Identifier = normal,

    override val interaction: GuiWidgetInteraction = GuiNoOpWidgetInteraction,
    @Compact override val tooltip: List<GuiWidgetTooltip>?,

    val x: GuiElementPosition,
    val y: GuiElementPosition,
    val width: Int,
    val height: Int,
    override val condition: GuiElementCondition?,
) : GuiWidgetElement {

    override val codec: MapCodec<GuiButtonElement> = KatharsisCodecs.getMapCodec<GuiButtonElement>()

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

@GenerateCodec
data class GuiItemStackElement(
    val item: ItemStackProvider,
    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.FOREGROUND,
    val x: GuiElementPosition = GuiElementPosition.START,
    val y: GuiElementPosition = GuiElementPosition.START,
    val width: Int = 16,
    val height: Int = 16,
    override val interaction: GuiWidgetInteraction = GuiNoOpWidgetInteraction,
    @Compact override val tooltip: List<GuiWidgetTooltip>?,
    override val condition: GuiElementCondition?,
) : GuiWidgetElement {
    override val codec: MapCodec<GuiItemStackElement> = KatharsisCodecs.getMapCodec<GuiItemStackElement>()

    override fun isHovered(mouseX: Int, mouseY: Int, bounds: ScreenRectangle): Boolean {
        val newX = x.calculate(bounds.left(), bounds.width())
        val newY = y.calculate(bounds.top(), bounds.height())
        return mouseX >= newX && mouseX <= newX + width && mouseY >= newY && mouseY <= newY + height
    }

    override fun getParsedTooltip(): List<Component>? {
        val customTooltip = super.getParsedTooltip()
        if (customTooltip != null) {
            return customTooltip
        }

        val stack = item.getItemStack()
        val itemTooltip = if (stack.isEmpty) emptyList() else stack.getLore()
        return itemTooltip.ifEmpty { null }
    }

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val stack = item.getItemStack()
        if (stack.isEmpty) return

        val newX = x.calculate(bounds.left(), bounds.width())
        val newY = y.calculate(bounds.top(), bounds.height())

        if (width != 16 || height != 16) {
            val scaleX = width / 16f
            val scaleY = height / 16f
            graphics.pose().pushMatrix()
            graphics.pose().translate(newX.toFloat(), newY.toFloat())
            graphics.pose().scale(scaleX, scaleY)
            graphics.item(stack, 0, 0, 0)
            graphics.pose().popMatrix()
        } else {
            graphics.item(stack, newX, newY, 0)
        }
    }
}
