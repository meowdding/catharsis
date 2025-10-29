package me.owdding.catharsis.utils.geometry

import net.minecraft.core.Direction
import org.joml.Vector2f
import org.joml.Vector3f

data class BakedBedrockVertex(
    val position: Vector3f,
    val uv: Vector2f,
)

data class BakedBedrockQuad(
    val vertices: List<BakedBedrockVertex>,
    val direction: Direction,
)

data class BakedBedrockCube(
    val pivot: Vector3f,
    val rotation: Vector3f,
    val quads: List<BakedBedrockQuad>,
)

data class BakedBedrockBone(
    val name: String,
    val parent: String?,

    var pivot: Vector3f,
    var rotation: Vector3f,
    val mirror: Boolean,
    val inflate: Float,
    val cubes: List<BakedBedrockCube>,

    val children: MutableList<BakedBedrockBone> = mutableListOf(),
)

data class BakedBedrockGeometry(
    val description: BedrockGeometryDescription,
    val bones: List<BakedBedrockBone>,
) {
    fun findByName(name: String) = bones.find { it.name == name }
}
