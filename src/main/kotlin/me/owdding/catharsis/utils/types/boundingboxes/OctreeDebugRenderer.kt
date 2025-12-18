package me.owdding.catharsis.utils.types.boundingboxes

import me.owdding.catharsis.utils.extensions.renderLineBox
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
            event.renderLineBox(it.toMinecraftAABB().move(-camX, -camY, -camZ))
        }

        val nodesRendered = MutableInt()
        val playerNode = octree.findLeaf(McPlayer.self?.blockPosition() ?: BlockPos.ZERO)
        octree.visitNode { node, depth ->
            visit(event, node, nodesRendered, depth, playerNode)
        }
    }

    fun visit(event: RenderWorldEvent, node: Node, nodesRendered: MutableInt, depth: Int, playerNode: Leaf?) {
        val aabb: AABB = node.getBox().toMinecraftAABB()
        val color = (aabb.xsize / 16.0).roundToInt()
        val colorValue = color + 5L
        val camX = event.cameraPosition.x
        val camY = event.cameraPosition.y
        val camZ = event.cameraPosition.z
        event.renderLineBox(
            aabb.move(-camX, -camY, -camZ),
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
