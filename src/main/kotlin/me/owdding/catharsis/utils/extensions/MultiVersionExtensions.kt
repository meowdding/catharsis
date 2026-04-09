package me.owdding.catharsis.utils.extensions

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.phys.AABB
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent

fun PoseStack.pose(): PoseStack.Pose = this.last()

fun RenderWorldEvent.renderLineBox(
    box: AABB,
    red: Float = 1f, green: Float = 1f, blue: Float = 1f, alpha: Float = 1f,
    secondary: Boolean = false,
) {
    //? >= 1.21.11 {
    val vertexConsumer: VertexConsumer = this.buffer.getBuffer(
        if (secondary) net.minecraft.client.renderer.rendertype.RenderTypes.SECONDARY_BLOCK_OUTLINE else net.minecraft.client.renderer.rendertype.RenderTypes.lines(),
    )
    ShapeRenderer.renderShape(
        this.poseStack, vertexConsumer,
        net.minecraft.world.phys.shapes.Shapes.create(
            box.minX - 0.005, box.minY - 0.005, box.minZ - 0.005,
            box.maxX + 0.005, box.maxY + 0.005, box.maxZ + 0.005,
        ),
        0.0, 0.0, 0.0, net.minecraft.util.ARGB.colorFromFloat(alpha, red, green, blue), 1f,
    )
    //?} else {

    /*val vertexConsumer: VertexConsumer = this.buffer.getBuffer(
        if (secondary) net.minecraft.client.renderer.RenderType.SECONDARY_BLOCK_OUTLINE else net.minecraft.client.renderer.RenderType.lines()
    )
    ShapeRenderer.renderLineBox(this.poseStack.pose(), vertexConsumer, box, red, green, blue, alpha)
    *///?}
}

fun MultiLineTextWidget.withClickHandler(handler: (Style) -> Unit): MultiLineTextWidget {
    //? if >=1.21.11 {
    this.setComponentClickHandler(handler)
    //?} else
     /*this.configureStyleHandling(true, handler)*/
    return this
}

fun <T : Any> CycleButtonBuilder(nameFactory: (T) -> Component, value: () -> T): CycleButton.Builder<T> {
    //? if >=1.21.11 {
    return CycleButton.builder(nameFactory, value)
    //?} else
     /*return CycleButton.builder(nameFactory).withInitialValue(value.invoke())*/
}
