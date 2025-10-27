package me.owdding.catharsis.utils.boundingboxes

import com.mojang.blaze3d.vertex.VertexConsumer
import me.owdding.catharsis.utils.extensions.pose
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import org.apache.commons.lang3.mutable.MutableInt
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import kotlin.math.roundToInt

object OctreeDebugRenderer {

    fun render(octree: Octree, event: RenderWorldEvent) {
        val camX = event.cameraPosition.x
        val camY = event.cameraPosition.y
        val camZ = event.cameraPosition.z
        octree.boxes.forEach {
            val vertexConsumer: VertexConsumer = event.buffer.getBuffer(RenderType.lines())
            ShapeRenderer.renderLineBox(
                event.poseStack.pose(),
                vertexConsumer,
                it.toMinecraftAABB().move(-camX, -camY, -camZ),
                1.0f,
                1.0f,
                1.0f,
                1f,
            )
        }

        val nodesRendered = MutableInt()
        val playerNode = octree.findLeaf(McPlayer.self?.blockPosition() ?: BlockPos.ZERO)
        octree.visitNode { node, depth ->
            visit(event, node, nodesRendered, depth, playerNode)
        }
    }

    fun visit(event: RenderWorldEvent, node: Node, nodesRendered: MutableInt, depth: Int, playerNode: Leaf?) {
        val AABB: AABB = node.getBox().toMinecraftAABB()
        val size = AABB.xsize
        val color = (size / 16.0).roundToInt()
        val vertexConsumer: VertexConsumer = event.buffer.getBuffer(RenderType.lines())
        val colorValue = color + 5L
        val camX = event.cameraPosition.x
        val camY = event.cameraPosition.y
        val camZ = event.cameraPosition.z
        ShapeRenderer.renderLineBox(
            event.poseStack.pose(),
            vertexConsumer,
            AABB.move(-camX, -camY, -camZ),
            getColorComponent(colorValue, 0.3f),
            getColorComponent(colorValue, 0.8f),
            getColorComponent(colorValue, 0.5f),
            if (node != playerNode) 0.4f else 1.0f,
        )
    }

    private fun getColorComponent(value: Long, multiplier: Float): Float {
        return Mth.frac((multiplier * value.toFloat())) * 0.9f + 0.1f
    }

}
