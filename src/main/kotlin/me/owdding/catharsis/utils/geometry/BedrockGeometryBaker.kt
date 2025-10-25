package me.owdding.catharsis.utils.geometry

import net.minecraft.core.Direction
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

object BedrockGeometryBaker {

    fun bake(geometry: BedrockGeometry): BakedBedrockGeometry {
        val parts = geometry.bones.associate { it.name to bakeBone(it) }
        parts.values.forEach { part -> part.parent?.let { parts[it]!!.children.add(part) } }

        return BakedBedrockGeometry(
            geometry.description,
            parts.values.filter { it.parent == null }
        )
    }

    private fun bakeBone(bone: BedrockBone): BakedBedrockBone {
        return BakedBedrockBone(
            bone.name,
            bone.parent,
            Vector3f(bone.pivot[0], bone.pivot[1], bone.pivot[2]),
            Vector3f(bone.rotation[0], bone.rotation[1], bone.rotation[2]),
            bone.mirror,
            bone.inflate,
            bone.cubes.map { bakeCube(bone, it) }
        )
    }

    private fun bakeCube(bone: BedrockBone, cube: BedrockCube): BakedBedrockCube {
        val uvs = cube.uv?.right()?.getOrNull() ?: error("Boxed UVs are not supported yet.")

        val pivot = Vector3f(cube.pivot[0], cube.pivot[1], cube.pivot[2])
        val rotation = Vector3f(cube.rotation[0], cube.rotation[1], cube.rotation[2])

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

        val up = bakeQuad(x0y1z0, x1y1z0, x1y1z1, x0y1z1, uvs, mirrored, Direction.UP)
        val down = bakeQuad(x0y0z0, x1y0z0, x1y0z1, x0y0z1, uvs, mirrored, Direction.DOWN)

        val north = bakeQuad(x0y1z0, x1y1z0, x1y0z0, x0y0z0, uvs, mirrored, Direction.NORTH)
        val south = bakeQuad(x0y1z1, x1y1z1, x1y0z1, x0y0z1, uvs, mirrored, Direction.SOUTH)

        val west = bakeQuad(x0y1z0, x0y1z1, x0y0z1, x0y0z0, uvs, mirrored, Direction.WEST)
        val east = bakeQuad(x1y1z0, x1y1z1, x1y0z1, x1y0z0, uvs, mirrored, Direction.EAST)

        return BakedBedrockCube(
            pivot,
            rotation,
            listOf(down, up, north, south, west, east)
        )
    }

    private fun bakeQuad(p1: Vector3f, p2: Vector3f, p3: Vector3f, p4: Vector3f, uvs: Map<Direction, UvFace>, mirror: Boolean, direction: Direction): BakedBedrockQuad {
        val direction = if (!mirror && direction.axis == Direction.Axis.X) direction.opposite else direction

        val uvs = uvs[direction]!!
        val v1 = BakedBedrockVertex(p1, bakeUv(uvs, 0, direction.axisDirection))
        val v2 = BakedBedrockVertex(p2, bakeUv(uvs, 1, direction.axisDirection))
        val v3 = BakedBedrockVertex(p3, bakeUv(uvs, 2, direction.axisDirection))
        val v4 = BakedBedrockVertex(p4, bakeUv(uvs, 3, direction.axisDirection))
        return BakedBedrockQuad(listOf(v1, v2, v3, v4), direction)
    }

    private fun bakeUv(face: UvFace, index: Int, dir: Direction.AxisDirection): Vector2f {
        return when (index) {
            0 -> Vector2f(face.uv[0], face.uv[1])
            1 -> Vector2f(face.uv[0] + face.uvSize[0], face.uv[1])
            2 -> Vector2f(face.uv[0] + face.uvSize[0], face.uv[1] + face.uvSize[1])
            3 -> Vector2f(face.uv[0], face.uv[1] + face.uvSize[1])
            else -> error("Invalid UV index: $index")
        }
    }
}
