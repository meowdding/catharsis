package me.owdding.catharsis.utils.geometry

import com.google.gson.JsonElement
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceParser
import me.owdding.catharsis.utils.geometry.armor.BedrockGeometryBaker
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.Direction
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import me.owdding.catharsis.utils.geometry.BakedBedrockGeometry as BakedBedrockArmorGeometry

@GenerateCodec
data class BedrockGeometry(
    val description: BedrockGeometryDescription,
    val bones: List<BedrockBone>,
) {

    fun bakeToArmor(): BakedBedrockArmorGeometry {
        return BedrockGeometryBaker.bake(this)
    }

    companion object {

        val RESOURCE_PARSER = TypedResourceParser.of<BedrockGeometry>(BedrockGeometry::parseSingle)
        val CODEC: Codec<List<BedrockGeometry>> = CatharsisCodecs.getCodec<BedrockGeometry>()
            .listOf()
            .fieldOf("minecraft:geometry")
            .codec()

        fun parseMulti(json: JsonElement): List<BedrockGeometry> {
            return json.toDataOrThrow(CODEC)
        }

        fun parseSingle(json: JsonElement): BedrockGeometry {
            return parseMulti(json).takeIf { it.size == 1 }?.first() ?: error("Expect one geometry but multiple were contained.")
        }
    }
}

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
    val pivot: Vector3f = Vector3f(),
    val rotation: Vector3f = Vector3f(),
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
    val pivot: Vector3f = Vector3f(),
    val rotation: Vector3f = Vector3f(),
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
