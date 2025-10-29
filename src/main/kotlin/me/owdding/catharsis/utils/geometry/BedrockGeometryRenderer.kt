package me.owdding.catharsis.utils.geometry

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.HumanoidModel
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import org.joml.*

private const val DEBUG = false

object BedrockGeometryRenderer {

    fun render(geometry: BakedBedrockGeometry, model: HumanoidModel<*>, pose: PoseStack.Pose, consumer: VertexConsumer, light: Int, overlay: Int) {
        pose.scale(1f, -1f, 1f)
        pose.translate(0f, -24f / 16f, 0f)

        for (bone in geometry.bones) {
            val humanBone = when (bone.name) {
                "head" -> model.head
                "body" -> model.body
                "right_arm" -> model.rightArm
                "left_arm" -> model.leftArm
                "right_leg" -> model.rightLeg
                "left_leg" -> model.leftLeg
                else -> null
            }

            humanBone?.let {
                bone.rotation.x = it.xRot * Mth.RAD_TO_DEG
                bone.rotation.y = it.yRot * Mth.RAD_TO_DEG
                bone.rotation.z = it.zRot * Mth.RAD_TO_DEG
            }

            renderBone(bone, pose, consumer, light, overlay)
        }
    }

    private fun renderBone(bone: BakedBedrockBone, pose: PoseStack.Pose, consumer: VertexConsumer, light: Int, overlay: Int) {
        val pose = pose.copy()

        val (rotX, rotY, rotZ) = bone.rotation
        val (pivotX, pivotY, pivotZ) = bone.pivot

        val quaternion = Quaternionf().rotateLocalX(-rotX * Mth.DEG_TO_RAD).rotateLocalY(rotY * Mth.DEG_TO_RAD).rotateLocalZ(rotZ * Mth.DEG_TO_RAD)
        pose.rotateAround(quaternion, pivotX / 16f, pivotY / 16f, pivotZ / 16f)

        for (cube in bone.cubes) {
            renderCube(cube, pose, consumer, light, overlay)
        }

        for (child in bone.children) {
            renderBone(child, pose, consumer, light, overlay)
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

    private fun renderCube(cube: BakedBedrockCube, pose: PoseStack.Pose, consumer: VertexConsumer, light: Int, overlay: Int) {
        val pose = pose.copy()

        val (rotX, rotY, rotZ) = cube.rotation
        val (pivotX, pivotY, pivotZ) = cube.pivot

        val quaternion = Quaternionf().rotateZYX(-rotZ * Mth.DEG_TO_RAD, rotY * Mth.DEG_TO_RAD, -rotX * Mth.DEG_TO_RAD)
            pose.rotateAround(quaternion, pivotX / 16f, pivotY / 16f, pivotZ / 16f)

        for (quad in cube.quads) {
            val normals = pose.transformNormal(quad.direction.opposite.step(), Vector3f())

            for (vertex in quad.vertices) {

                if (DEBUG) {
                    consumer
                        .addVertex(pose, vertex.position.x / 16f, vertex.position.y / 16f, vertex.position.z / 16f)
                        .setColor(quad.direction.color())
                } else {
                    consumer
                        .addVertex(pose, vertex.position.x / 16f, vertex.position.y / 16f, vertex.position.z / 16f)
                        .setColor(-1)
                        .setUv(vertex.uv.x, vertex.uv.y)
                        .setOverlay(overlay)
                        .setLight(light)
                        .setNormal(pose, normals.x(), normals.y(), normals.z())
                }
            }
        }
    }
}
