package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.InventoryScreen
//~ if >= 26.1 'client.renderer.LightTexture as LightCoordsUtil' -> 'util.LightCoordsUtil'
import net.minecraft.util.LightCoordsUtil
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.CommonColors
import org.joml.Quaternionf
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

@GenerateCodec
data class GuiPlayerElement(
    val rotation: Quaternionf?,
    val x: GuiElementPosition,
    val y: GuiElementPosition,
    val width: Int,
    val height: Int,
) : GuiElement {

    override val codec: MapCodec<GuiPlayerElement> = CatharsisCodecs.getMapCodec<GuiPlayerElement>()
    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.BACKGROUND

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val newX = x.calculate(bounds.left(), bounds.width())
        val newY = y.calculate(bounds.top(), bounds.height())

        val player = McPlayer.self ?: return

        if (rotation == null) {

            //~ if >= 26.1 'renderEntityInInventoryFollowsMouse' -> 'extractEntityInInventoryFollowsMouse'
            InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                newX, newY, newX + width, newY + height,
                30, 0.0625f,
                mouseX.toFloat(), mouseY.toFloat(),
                player
            )
        } else {
            val offset = Vector3f(0.0F, player.bbHeight / 2.0f + 0.0625f * player.scale, 0.0F)
            val state = McClient.self.entityRenderDispatcher.getRenderer(player).createRenderState(player, 1f)
            state.lightCoords = LightCoordsUtil.FULL_BRIGHT
            state.shadowPieces.clear()
            state.outlineColor = 0

            //~ if >= 26.1 'submitEntityRenderState(' -> 'entity('
            graphics.entity(state, 25.0F, offset, rotation, null, newX, newY, newX + width, newY + height)
        }
    }
}

@GenerateCodec
data class GuiSpriteElement(
    val sprite: Identifier,
    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.BACKGROUND,
    val x: GuiElementPosition = GuiElementPosition.START,
    val y: GuiElementPosition = GuiElementPosition.START,
    val width: Int?,
    val height: Int?,
) : GuiElement {

    override val codec: MapCodec<GuiSpriteElement> = CatharsisCodecs.getMapCodec<GuiSpriteElement>()

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED, sprite,
            x.calculate(bounds.left(), bounds.width()), y.calculate(bounds.top(), bounds.height()),
            width ?: bounds.width(), height ?: bounds.height()
        )
    }

}


@GenerateCodec
data class GuiTextElement(
    val text: Component,
    val color: Int = CommonColors.DARK_GRAY,
    val x: GuiElementPosition,
    val y: GuiElementPosition,
    val alignment: Float = 0f,
) : GuiElement {

    override val codec: MapCodec<GuiSpriteElement> = CatharsisCodecs.getMapCodec<GuiSpriteElement>()

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val newX = x.calculate(bounds.left(), bounds.width()) - (McFont.width(text) * alignment).toInt()
        val newY = y.calculate(bounds.top(), bounds.height())
        //~ if >= 26.1 'drawString(' -> 'text('
        graphics.text(McFont.self, text, newX, newY, this.color)
    }

}
