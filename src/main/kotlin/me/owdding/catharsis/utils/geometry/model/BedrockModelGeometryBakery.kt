package me.owdding.catharsis.utils.geometry.model

import com.mojang.blaze3d.vertex.PoseStack
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.models.BedrockModels
import me.owdding.catharsis.utils.extensions.pose
import me.owdding.catharsis.utils.geometry.*
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

data class BakedBedrockModelGeometry(
    val bones: List<BakedBedrockModelBonePart>,
)

data class ModelBone(
    val base: BedrockBone,
    val parent: String? = base.parent,
    val children: MutableList<ModelBone> = mutableListOf(),
)

data class CulledQuad(
    val bakedQuad: BakedQuad,
    val occlusionCulling: Boolean,
)

object BedrockModelGeometryBaker {
    val logger = Catharsis.featureLogger("BedrockModelGeometryBaker")

    fun bakeBones(model: ResourceLocation, atlasSprite: TextureAtlasSprite, ambientOcclusion: Boolean?): BakedBedrockModelGeometry = runCatching {
        bake(BedrockModels.getModel(model)!!, atlasSprite, ambientOcclusion)
    }.getOrElse {
        throw RuntimeException("Failed to create geo model $model", it)
    }

    fun bake(geometry: BedrockGeometry, atlasSprite: TextureAtlasSprite, ambientOcclusion: Boolean?): BakedBedrockModelGeometry {
        val parts = geometry.bones.associate { it.name to ModelBone(it) }
        parts.values.forEach { part -> part.parent?.let { parts[it]!!.children.add(part) } }

        val rootBones = parts.values.filter { it.parent == null }

        val stack = PoseStack()
        stack.translate(8.0,0.0,8.0)
        val bakedBones = rootBones.map { bakeBone(it, stack, geometry.description, atlasSprite, 0f, ambientOcclusion) }

        if (!stack.isEmpty) {
            logger.warn("Pose stack not empty after baking of $geometry")
        }
        return BakedBedrockModelGeometry(bakedBones)
    }

    private fun bakeBone(
        modelBone: ModelBone,
        stack: PoseStack,
        description: BedrockGeometryDescription,
        atlasSprite: TextureAtlasSprite,
        inflation: Float,
        ambientOcclusion: Boolean?
    ): BakedBedrockModelBonePart {
        stack.pushPose()
        val base = modelBone.base
        val newInflation = inflation + base.inflate
        val pivot = base.pivot
        val rotation = base.rotation.mul(Mth.DEG_TO_RAD, Vector3f())
        stack.rotateAround(Quaternionf().rotateZYX(rotation.z, rotation.y, rotation.x), pivot.x, pivot.y, pivot.z)

        val quads = base.cubes.flatMap { bakeCube(base, it, stack, description, atlasSprite, newInflation) }
        val bones = modelBone.children.map { bakeBone(it, stack, description, atlasSprite, newInflation, ambientOcclusion) }

        stack.popPose()
        return BakedBedrockModelBonePart(quads, bones, ambientOcclusion)
    }

    private fun bakeCube(bone: BedrockBone, cube: BedrockCube, stack: PoseStack, description: BedrockGeometryDescription, atlasSprite: TextureAtlasSprite, inflation: Float): List<CulledQuad> {
        val uvs = cube.uv?.right()?.getOrNull() ?: TODO("Boxed UVs are not supported yet.")
        stack.pushPose()

        val pivot = cube.pivot
        val rotation = cube.rotation.mul(Mth.DEG_TO_RAD, Vector3f())
        stack.rotateAround(Quaternionf().rotateZYX(rotation.z, rotation.y, rotation.x), pivot.x, pivot.y, pivot.z)

        val inflation = cube.inflate?.plus(inflation) ?: inflation
        val mirrored = cube.mirror ?: bone.mirror

        var minX = cube.origin[0] - inflation
        val minY = cube.origin[1] - inflation
        val minZ = cube.origin[2] - inflation
        var maxX = cube.origin[0] + cube.size[0] + inflation
        val maxY = cube.origin[1] + cube.size[1] + inflation
        val maxZ = cube.origin[2] + cube.size[2] + inflation

        if (mirrored) {
            val temp = minX
            minX = maxX
            maxX = temp
        }

        val x0y0z0 = Vector3f(minX, minY, minZ)
        val x1y0z0 = Vector3f(maxX, minY, minZ)
        val x1y0z1 = Vector3f(maxX, minY, maxZ)
        val x0y0z1 = Vector3f(minX, minY, maxZ)

        val x0y1z0 = Vector3f(minX, maxY, minZ)
        val x1y1z0 = Vector3f(maxX, maxY, minZ)
        val x1y1z1 = Vector3f(maxX, maxY, maxZ)
        val x0y1z1 = Vector3f(minX, maxY, maxZ)

        val up = bakeQuad(stack, atlasSprite, x0y1z0, x1y1z0, x1y1z1, x0y1z1, uvs, mirrored, Direction.UP, description)
        val down = bakeQuad(stack, atlasSprite, x0y0z0, x1y0z0, x1y0z1, x0y0z1, uvs, mirrored, Direction.DOWN, description)

        val north = bakeQuad(stack, atlasSprite, x0y1z0, x1y1z0, x1y0z0, x0y0z0, uvs, mirrored, Direction.NORTH, description)
        val south = bakeQuad(stack, atlasSprite, x0y1z1, x1y1z1, x1y0z1, x0y0z1, uvs, mirrored, Direction.SOUTH, description)

        val west = bakeQuad(stack, atlasSprite, x0y1z0, x0y1z1, x0y0z1, x0y0z0, uvs, mirrored, Direction.WEST, description)
        val east = bakeQuad(stack, atlasSprite, x1y1z0, x1y1z1, x1y0z1, x1y0z0, uvs, mirrored, Direction.EAST, description)

        stack.popPose()
        return listOf(down, up, north, south, west, east)
    }

    private fun bakeQuad(
        stack: PoseStack, atlasSprite: TextureAtlasSprite,
        p1: Vector3f, p2: Vector3f, p3: Vector3f, p4: Vector3f,
        uvs: Map<Direction, UvFace>, mirror: Boolean, direction: Direction, description: BedrockGeometryDescription,
    ): CulledQuad {
        val direction = if (!mirror && direction.axis == Direction.Axis.X) direction.opposite else direction

        val actualDirection = Direction.rotate(stack.pose().pose(), direction)

        val data = IntArray(32)

        val uvOffset = if (actualDirection.axisDirection == Direction.AxisDirection.POSITIVE) 1 else 0
        val vertexOffset = if (direction.axisDirection == Direction.AxisDirection.POSITIVE) 4 else 0
        val uvs = uvs[direction]!!
        val p1 = stack.pose().pose().transformPosition(p1, Vector3f())
        val p2 = stack.pose().pose().transformPosition(p2, Vector3f())
        val p3 = stack.pose().pose().transformPosition(p3, Vector3f())
        val p4 = stack.pose().pose().transformPosition(p4, Vector3f())
        val f1 = abs(getCoordinate(actualDirection, p1) - 8)
        val f2 = abs(getCoordinate(actualDirection, p2) - 8)
        val f3 = abs(getCoordinate(actualDirection, p3) - 8)
        val f4 = abs(getCoordinate(actualDirection, p4) - 8)

        val occlusionCulling = (f1 == 8f && f1 == f2 && f2 == f3 && f3 == f4)

        packVertex(data, abs(vertexOffset - 0) % 4, p1, bakeUv(uvs, 0 + uvOffset, description), atlasSprite)
        packVertex(data, abs(vertexOffset - 1) % 4, p2, bakeUv(uvs, 1 - uvOffset, description), atlasSprite)
        packVertex(data, abs(vertexOffset - 2) % 4, p3, bakeUv(uvs, 2 + uvOffset, description), atlasSprite)
        packVertex(data, abs(vertexOffset - 3) % 4, p4, bakeUv(uvs, 3 - uvOffset, description), atlasSprite)
        return CulledQuad(BakedQuad(data, -1, if (actualDirection.axis == Direction.Axis.X) actualDirection.opposite else actualDirection, atlasSprite, true, 0), occlusionCulling)
    }

    fun getCoordinate(direction: Direction, vector3f: Vector3f) = when (direction.axis) {
        Direction.Axis.X -> vector3f.x
        Direction.Axis.Y -> vector3f.y
        Direction.Axis.Z -> vector3f.z
    }

    private fun packVertex(
        data: IntArray,
        index: Int,
        pos: Vector3f,
        uv: Vector2f,
        atlasSprite: TextureAtlasSprite,
    ) {
        val offset = index * 8
        data[offset + 0] = (pos.x / 16).toRawBits()
        data[offset + 1] = (pos.y / 16).toRawBits()
        data[offset + 2] = (pos.z / 16).toRawBits()
        data[offset + 3] = -1 // color
        data[offset + 4] = atlasSprite.getU(uv.x).toRawBits()
        data[offset + 5] = atlasSprite.getV(uv.y).toRawBits()
    }

    private fun bakeUv(face: UvFace, index: Int, description: BedrockGeometryDescription): Vector2f {
        return when (index % 4) {
            0 -> Vector2f(face.uv[0], face.uv[1])
            1 -> Vector2f(face.uv[0] + face.uvSize[0], face.uv[1])
            2 -> Vector2f(face.uv[0] + face.uvSize[0], face.uv[1] + face.uvSize[1])
            3 -> Vector2f(face.uv[0], face.uv[1] + face.uvSize[1])
            else -> error("Invalid UV index: $index")
        }.div(description.textureWidth.toFloat(), description.textureHeight.toFloat())
    }
}
