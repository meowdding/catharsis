package me.owdding.catharsis.utils.geometry.model

import com.mojang.blaze3d.vertex.PoseStack
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.models.BedrockModels
import me.owdding.catharsis.utils.geometry.*
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.jvm.optionals.getOrNull

data class BakedBedrockModelGeometry(
    val bones: List<BakedBedrockModelBonePart>,
)

data class ModelBone(
    val base: BedrockBone,
    val parent: String? = base.parent,
    val children: MutableList<ModelBone> = mutableListOf(),
)

data class MediumBakedQuad(
    val data: IntArray,
    val direction: Direction,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediumBakedQuad) return false

        if (!data.contentEquals(other.data)) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + direction.hashCode()
        return result
    }
}

data class MediumBakedBedrockGeometryBone(
    val quads: List<MediumBakedQuad>,
    val bones: List<MediumBakedBedrockGeometryBone>,
)

data class MediumBakedBedrockModelGeometry(
    val bones: List<MediumBakedBedrockGeometryBone>,
)

object BedrockModelGeometryBaker {
    val logger = Catharsis.featureLogger("BedrockModelGeometryBaker")

    fun bakeBones(model: ResourceLocation, atlasSprite: TextureAtlasSprite): BakedBedrockModelGeometry = fry(BedrockModels.getModel(model)!!, atlasSprite)

    fun fry(geometry: MediumBakedBedrockModelGeometry, atlasSprite: TextureAtlasSprite): BakedBedrockModelGeometry {
        return BakedBedrockModelGeometry(geometry.bones.map { fryBone(it, atlasSprite) })
    }

    fun fryBone(bone: MediumBakedBedrockGeometryBone, atlasSprite: TextureAtlasSprite): BakedBedrockModelBonePart = BakedBedrockModelBonePart(
        bone.quads.map { fryQuad(it, atlasSprite) }, bone.bones.map { fryBone(it, atlasSprite) }
    )

    fun fryQuad(quad: MediumBakedQuad, atlasSprite: TextureAtlasSprite) = BakedQuad(quad.data, 0, quad.direction, atlasSprite, false, 0)

    fun bake(geometry: BedrockGeometry): MediumBakedBedrockModelGeometry {
        val parts = geometry.bones.associate { it.name to ModelBone(it) }
        parts.values.forEach { part -> part.parent?.let { parts[it]!!.children.add(part) } }

        val rootBones = parts.values.filter { it.parent == null }

        val stack = PoseStack()
        val bakedBones = rootBones.map { bakeBone(it, stack, geometry.description) }

        if (!stack.isEmpty) {
            logger.warn("Pose stack not empty after baking of $geometry")
        }
        return MediumBakedBedrockModelGeometry(bakedBones)
    }

    private fun bakeBone(modelBone: ModelBone, stack: PoseStack, description: BedrockGeometryDescription): MediumBakedBedrockGeometryBone {
        stack.pushPose()
        val base = modelBone.base
        val pivot = base.pivot
        val rotation = base.rotation
        stack.rotateAround(Quaternionf().rotateZYX(rotation.z, rotation.y, rotation.x), pivot.x, pivot.y, pivot.z)

        val quads = base.cubes.flatMap { bakeCube(base, it, stack, description) }
        val bones = modelBone.children.map { bakeBone(it, stack, description) }

        stack.popPose()
        return MediumBakedBedrockGeometryBone(quads, bones)
    }

    private fun bakeCube(bone: BedrockBone, cube: BedrockCube, stack: PoseStack, description: BedrockGeometryDescription): List<MediumBakedQuad> {
        val uvs = cube.uv?.right()?.getOrNull() ?: TODO("Boxed UVs are not supported yet.")
        stack.pushPose()

        val pivot = cube.pivot
        val rotation = cube.rotation
        stack.rotateAround(Quaternionf().rotateZYX(rotation.z, rotation.y, rotation.x), pivot.x, pivot.y, pivot.z)

        val inflation = cube.inflate ?: bone.inflate
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

        val up = bakeQuad(stack, x0y1z0, x1y1z0, x1y1z1, x0y1z1, uvs, mirrored, Direction.UP, description)
        val down = bakeQuad(stack, x0y0z0, x1y0z0, x1y0z1, x0y0z1, uvs, mirrored, Direction.DOWN, description)

        val north = bakeQuad(stack, x0y1z0, x1y1z0, x1y0z0, x0y0z0, uvs, mirrored, Direction.NORTH, description)
        val south = bakeQuad(stack, x0y1z1, x1y1z1, x1y0z1, x0y0z1, uvs, mirrored, Direction.SOUTH, description)

        val west = bakeQuad(stack, x0y1z0, x0y1z1, x0y0z1, x0y0z0, uvs, mirrored, Direction.WEST, description)
        val east = bakeQuad(stack, x1y1z0, x1y1z1, x1y0z1, x1y0z0, uvs, mirrored, Direction.EAST, description)

        stack.popPose()
        return listOf(down, up, north, south, west, east)
    }

    private fun bakeQuad(
        stack: PoseStack,
        p1: Vector3f, p2: Vector3f, p3: Vector3f, p4: Vector3f,
        uvs: Map<Direction, UvFace>, mirror: Boolean, direction: Direction, description: BedrockGeometryDescription,
    ): MediumBakedQuad {
        val direction = if (!mirror && direction.axis == Direction.Axis.X) direction.opposite else direction

        val data = IntArray(32)

        val uvOffset = if (direction.axisDirection == Direction.AxisDirection.POSITIVE) 1 else 0
        val uvs = uvs[direction]!!
        packVertex(data, 0, p1, bakeUv(uvs, 0 + uvOffset, description))
        packVertex(data, 1, p2, bakeUv(uvs, 1 - uvOffset, description))
        packVertex(data, 2, p3, bakeUv(uvs, 2 + uvOffset, description))
        packVertex(data, 3, p4, bakeUv(uvs, 3 - uvOffset, description))
        return MediumBakedQuad(data, direction)
    }

    private fun packVertex(
        data: IntArray,
        index: Int,
        pos: Vector3f,
        uv: Vector2f,
    ) {
        val offset = index * 8
        data[offset + 0] = pos.x.toRawBits()
        data[offset + 1] = pos.y.toRawBits()
        data[offset + 2] = pos.z.toRawBits()
        data[offset + 3] = -1 // color
        data[offset + 4] = uv.x.toRawBits()
        data[offset + 5] = uv.y.toRawBits()
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
