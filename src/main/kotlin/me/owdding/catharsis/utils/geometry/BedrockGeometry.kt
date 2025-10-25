package me.owdding.catharsis.utils.geometry

import com.mojang.datafixers.util.Either
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.Direction

@GenerateCodec
data class BedrockGeometry(
    val description: BedrockGeometryDescription,
    val bones: List<BedrockBone>,
)

@GenerateCodec
data class BedrockGeometryDescription(
    val identifier: String,
    @FieldName("texture_width") val textureWidth: Int,
    @FieldName("texture_height") val textureHeight: Int,
)

@GenerateCodec
data class BedrockBone(
    val name: String,
    val parent: String?,
    val pivot: List<Float> = listOf(0f, 0f, 0f),
    val rotation: List<Float> = listOf(0f, 0f, 0f),
    val mirror: Boolean = false,
    val inflate: Float = 0f,
    // debug, the spec has no mention of what its purpose is
    // render_group_id, this is also not documented what its purpose is
    val cubes: List<BedrockCube> = listOf(),
    // locators, allows for locating specific points on the model, we don't need this
    // poly_mesh, this is for custom meshes, way beyond the scope of this project
    // texture_meshes, also for custom meshes
)

@GenerateCodec
data class BedrockCube(
    val origin: List<Float>,
    val size: List<Float>,
    val rotation: List<Float> = listOf(0f, 0f, 0f),
    val pivot: List<Float> = listOf(0f, 0f, 0f),
    val inflate: Float?,
    val mirror: Boolean?,
    val uv: Either<List<Float>, Map<Direction, UvFace>>?
)

@GenerateCodec
data class UvFace(
    val uv: List<Float>,
    @FieldName("uv_size") val uvSize: List<Float>,
    // material_instance, seemingly only used for blocks
)
