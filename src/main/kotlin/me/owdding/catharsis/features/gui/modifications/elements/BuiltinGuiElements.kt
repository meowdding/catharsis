package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.CommonColors
import org.joml.Quaternionf
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

@GenerateCodec
data class GuiPlayerElement(
    val rotation: Quaternionf?,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
) : GuiElement {

    override val codec: MapCodec<GuiPlayerElement> = CatharsisCodecs.getMapCodec<GuiPlayerElement>()
    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.BACKGROUND

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val newX = bounds.left() + x
        val newY = bounds.top() + y

        val player = McPlayer.self ?: return

        if (rotation == null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                newX, newY, newX + width, newY + height,
                30, 0.0625f,
                mouseX.toFloat(), mouseY.toFloat(),
                player
            )
        } else {
            val offset = Vector3f(0.0F, player.bbHeight / 2.0f + 0.0625f * player.scale, 0.0F)
            InventoryScreen.renderEntityInInventory(
                graphics,
                newX, newY, newX + width, newY + height,
                30 / player.scale,
                offset, rotation, null,
                player
            )
        }
    }
}

@GenerateCodec
data class GuiSpriteElement(
    val sprite: ResourceLocation,
    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.BACKGROUND,
    val x: Int?,
    val y: Int?,
    val width: Int?,
    val height: Int?,
) : GuiElement {

    override val codec: MapCodec<GuiSpriteElement> = CatharsisCodecs.getMapCodec<GuiSpriteElement>()

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED, sprite,
            bounds.left() + (x ?: 0), bounds.top() + (y ?: 0),
            width ?: bounds.width(), height ?: bounds.height(),
        )
    }

}


@GenerateCodec
data class GuiTextElement(
    val text: Component,
    val color: Int = CommonColors.DARK_GRAY,
    val x: Int,
    val y: Int,
    val alignment: Float = 0f,
) : GuiElement {

    override val codec: MapCodec<GuiSpriteElement> = CatharsisCodecs.getMapCodec<GuiSpriteElement>()

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val x = bounds.left() + this.x
        val y = bounds.top() + this.y
        graphics.drawString(McFont.self, text, (x - McFont.width(text) * alignment).toInt(), y, this.color)
    }

}
