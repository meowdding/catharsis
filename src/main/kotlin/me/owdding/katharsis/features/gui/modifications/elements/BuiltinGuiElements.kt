package me.owdding.katharsis.features.gui.modifications.elements

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.gui.modifications.elements.conditions.GuiElementCondition
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.CommonColors
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
//? > 26.1
import net.minecraft.world.entity.EntitySpawnRequest
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import org.joml.Quaternionf
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

abstract class AbstractGuiEntityElement() : GuiElement {
    abstract val rotation: Quaternionf?
    abstract val x: GuiElementPosition
    abstract val y: GuiElementPosition
    abstract val width: Int
    abstract val height: Int

    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.BACKGROUND

    abstract fun getEntity(): Entity?

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val newX = x.calculate(bounds.left(), bounds.width())
        val newY = y.calculate(bounds.top(), bounds.height())

        val entity = getEntity() ?: return
        val isLiving = entity is LivingEntity

        if (rotation == null && isLiving) {
            InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                newX, newY, newX + width, newY + height,
                30, 0.0625f,
                mouseX.toFloat(), mouseY.toFloat(),
                entity,
            )
        } else {
            val scale = if (isLiving) entity.scale else 1f
            val offset = Vector3f(0.0F, entity.bbHeight / 2.0f + 0.0625f * scale, 0.0F)
            val state = McClient.self.entityRenderDispatcher.getRenderer(entity).createRenderState(entity, 1f)
            state.lightCoords = LightCoordsUtil.FULL_BRIGHT
            state.shadowPieces.clear()
            state.outlineColor = 0
            graphics.entity(state, 25.0F, offset, rotation, null, newX, newY, newX + width, newY + height)
        }
    }
}

@GenerateCodec
data class GuiPlayerElement(
    override val rotation: Quaternionf?,
    override val x: GuiElementPosition,
    override val y: GuiElementPosition,
    override val width: Int,
    override val height: Int,
    override val condition: GuiElementCondition?,
) : AbstractGuiEntityElement() {
    override val codec: MapCodec<GuiPlayerElement> = KatharsisCodecs.getMapCodec<GuiPlayerElement>()

    override fun getEntity(): LivingEntity? = McPlayer.self
}

@GenerateCodec
data class GuiEntityElement(
    override val rotation: Quaternionf?,
    override val x: GuiElementPosition,
    override val y: GuiElementPosition,
    override val width: Int,
    override val height: Int,
    override val condition: GuiElementCondition?,
    val entityType: EntityType<*>,
    val tag: CompoundTag = CompoundTag(),
) : AbstractGuiEntityElement() {
    override val codec: MapCodec<GuiEntityElement> = KatharsisCodecs.getMapCodec<GuiEntityElement>()

    private val parsedEntity: Entity? by lazy {
        //? >= 26.2 {
        val thing = EntitySpawnRequest(EntitySpawnReason.COMMAND, true)
        //?} else
        //val thing = EntitySpawnReason.COMMAND
        tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString())
        EntityType.loadEntityRecursive(tag, McLevel.self!!, thing) {
            it.id = -1
            it
        }
    }

    override fun getEntity() = parsedEntity
}

@GenerateCodec
data class GuiSpriteElement(
    val sprite: Identifier,
    override val layer: GuiElementRenderLayer = GuiElementRenderLayer.BACKGROUND,
    val x: GuiElementPosition = GuiElementPosition.START,
    val y: GuiElementPosition = GuiElementPosition.START,
    val width: Int?,
    val height: Int?,
    override val condition: GuiElementCondition?,
) : GuiElement {

    override val codec: MapCodec<GuiSpriteElement> = KatharsisCodecs.getMapCodec<GuiSpriteElement>()

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED, sprite,
            x.calculate(bounds.left(), bounds.width()), y.calculate(bounds.top(), bounds.height()),
            width ?: bounds.width(), height ?: bounds.height(),
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
    override val condition: GuiElementCondition?,
) : GuiElement {

    override val codec: MapCodec<GuiSpriteElement> = KatharsisCodecs.getMapCodec<GuiSpriteElement>()

    override fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float, bounds: ScreenRectangle) {
        val newX = x.calculate(bounds.left(), bounds.width()) - (McFont.width(text) * alignment).toInt()
        val newY = y.calculate(bounds.top(), bounds.height())
        graphics.text(McFont.self, text, newX, newY, this.color)
    }

}
