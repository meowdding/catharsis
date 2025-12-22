package me.owdding.catharsis.utils.types.boundingboxes

import me.owdding.catharsis.utils.extensions.renderLineBox
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import org.apache.commons.lang3.mutable.MutableInt
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McPlayer.contains
import kotlin.math.abs
import kotlin.math.min
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
        val playerPosition: BlockPos = McPlayer.self?.blockPosition() ?: BlockPos.ZERO
        val playerNode = octree.findLeaf(playerPosition)
        octree.visitNode { node, depth ->
            visit(event, node, nodesRendered, depth, playerNode, playerPosition)
        }
    }

    fun visit(event: RenderWorldEvent, node: Node, nodesRendered: MutableInt, depth: Int, playerNode: Leaf?, playerPosition: BlockPos, renderDistance: Int = 25) {
        val aabb: AABB = node.getBox().toMinecraftAABB()
        if (
            (min(
                abs(aabb.maxX - playerPosition.x),
                abs(aabb.minX - playerPosition.x),
            ) > renderDistance || min(
                abs(aabb.maxY - playerPosition.y),
                abs(aabb.minY - playerPosition.y),
            ) > renderDistance || min(
                abs(aabb.maxZ - playerPosition.z),
                abs(aabb.minZ - playerPosition.z),
            ) > renderDistance) && !aabb.contains(McPlayer)
        ) return
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
