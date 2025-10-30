package me.owdding.catharsis.utils.geometry.armor

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import me.owdding.catharsis.utils.debugToggle
import me.owdding.catharsis.utils.geometry.BakedBedrockBone
import me.owdding.catharsis.utils.geometry.BakedBedrockCube
import me.owdding.catharsis.utils.geometry.BakedBedrockGeometry
import net.minecraft.client.model.HumanoidModel
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import org.joml.*


private const val HEAD_BONE = "head"
private const val BODY_BONE = "body"
private const val RIGHT_ARM_BONE = "right_arm"
private const val LEFT_ARM_BONE = "left_arm"
private const val RIGHT_LEG_BONE = "right_leg"
private const val LEFT_LEG_BONE = "left_leg"
private const val RIGHT_FOOT_BONE = "right_foot"
private const val LEFT_FOOT_BONE = "left_foot"

object BedrockArmorGeometryRenderer {
    private val debug by debugToggle("armor_debug", "Enables debug colors :3")

    @JvmStatic
    fun render(geometry: BakedBedrockGeometry, slot: EquipmentSlot, model: HumanoidModel<*>, pose: PoseStack.Pose, consumer: VertexConsumer, light: Int, overlay: Int) {
        pose.scale(1f, -1f, 1f)
        pose.translate(0f, -24f / 16f, 0f)

        for (bone in geometry.bones) {
            val playerBone = when (bone.name) {
                HEAD_BONE -> model.head
                BODY_BONE -> model.body
                RIGHT_ARM_BONE -> model.rightArm
                LEFT_ARM_BONE -> model.leftArm
                RIGHT_LEG_BONE, RIGHT_FOOT_BONE -> model.rightLeg
                LEFT_LEG_BONE, LEFT_FOOT_BONE -> model.leftLeg
                else -> continue
            }

            when (slot) {
                EquipmentSlot.HEAD if bone.name != HEAD_BONE -> continue
                EquipmentSlot.CHEST if bone.name != BODY_BONE && bone.name != RIGHT_ARM_BONE && bone.name != LEFT_ARM_BONE -> continue
                EquipmentSlot.LEGS if bone.name != RIGHT_LEG_BONE && bone.name != LEFT_LEG_BONE -> continue
                EquipmentSlot.FEET if bone.name != RIGHT_FOOT_BONE && bone.name != LEFT_FOOT_BONE -> continue
                else -> {}
            }

            bone.offset.x = (playerBone.x - playerBone.initialPose.x)
            bone.offset.y = -(playerBone.y - playerBone.initialPose.y)
            bone.offset.z = (playerBone.z - playerBone.initialPose.z)
            bone.rotation.x = playerBone.xRot * Mth.RAD_TO_DEG
            bone.rotation.y = playerBone.yRot * Mth.RAD_TO_DEG
            bone.rotation.z = playerBone.zRot * Mth.RAD_TO_DEG

            renderBone(bone, pose, consumer, light, overlay)
        }
    }

    private fun renderBone(bone: BakedBedrockBone, pose: PoseStack.Pose, consumer: VertexConsumer, light: Int, overlay: Int) {
        val pose = pose.copy()

        val (rotX, rotY, rotZ) = bone.rotation
        val (pivotX, pivotY, pivotZ) = bone.pivot
        val (posX, posY, posZ) = bone.offset

        pose.translate(posX / 16f, posY / 16f, posZ / 16f)
        val quaternion = Quaternionf().rotateZYX(-rotZ * Mth.DEG_TO_RAD, rotY * Mth.DEG_TO_RAD, -rotX * Mth.DEG_TO_RAD)
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
            val normals = pose.transformNormal(quad.direction.step(), Vector3f())

            for (vertex in quad.vertices) {

                if (debug) {
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
