package me.owdding.catharsis.utils.geometry

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import org.joml.Quaternionf
import org.joml.component1
import org.joml.component2
import org.joml.component3

private const val DEBUG = false

object BedrockGeometryRenderer {

    fun render(geometry: BakedBedrockGeometry, pose: PoseStack.Pose, consumer: VertexConsumer) {
        pose.scale(1f, -1f, 1f)
        pose.translate(0f, -24f / 16f, 0f)

        for (bone in geometry.bones) {
            renderBone(bone, pose, consumer)
        }
    }

    private fun renderBone(bone: BakedBedrockBone, pose: PoseStack.Pose, consumer: VertexConsumer) {
        val pose = pose.copy()

        val (rotX, rotY, rotZ) = bone.rotation
        val (pivotX, pivotY, pivotZ) = bone.pivot

        val quaternion = Quaternionf().rotateXYZ(-rotX * Mth.DEG_TO_RAD, rotY * Mth.DEG_TO_RAD, rotZ * Mth.DEG_TO_RAD)
        pose.rotateAround(quaternion, pivotX / 16f, pivotY / 16f, pivotZ / 16f)

        for (cube in bone.cubes) {
            renderCube(cube, pose, consumer)
        }

        for (child in bone.children) {
            renderBone(child, pose, consumer)
        }
    }

    private fun Direction.color(): Int {
        return when (this) {
            Direction.UP -> 0xFFECF8FD.toInt()
            Direction.DOWN -> 0xFF6E788C.toInt()

            Direction.NORTH -> 0xFF5BBCF4.toInt()
            Direction.SOUTH -> 0xFFF8DD72.toInt()

            Direction.WEST -> 0xFFF48686.toInt()
            Direction.EAST -> 0xFF43E88D.toInt()
        }
    }

    private fun renderCube(cube: BakedBedrockCube, pose: PoseStack.Pose, consumer: VertexConsumer) {
        val pose = pose.copy()

        val (rotX, rotY, rotZ) = cube.rotation
        val (pivotX, pivotY, pivotZ) = cube.pivot

        val quaternion = Quaternionf().rotateXYZ(-rotX * Mth.DEG_TO_RAD, rotY * Mth.DEG_TO_RAD, rotZ * Mth.DEG_TO_RAD)
        pose.rotateAround(quaternion, pivotX / 16f, pivotY / 16f, pivotZ / 16f)

        for (quad in cube.quads) {
            for (vertex in quad.vertices) {

                if (DEBUG) {
                    consumer
                        .addVertex(pose, vertex.position.x / 16f, vertex.position.y / 16f, vertex.position.z / 16f)
                        .setColor(quad.direction.color())
                } else {
                    val u = if (vertex.uv.x < 0f) 64f - vertex.uv.x else vertex.uv.x
                    val v = if (vertex.uv.y < 0f) 64f - vertex.uv.y else vertex.uv.y

                    consumer
                        .addVertex(pose, vertex.position.x / 16f, vertex.position.y / 16f, vertex.position.z / 16f)
                        .setColor(-1)
                        .setUv(u / 64f, v / 64f)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightTexture.FULL_BRIGHT)
                        .setNormal(pose, quad.direction.step())
                }
            }
        }
    }
}
