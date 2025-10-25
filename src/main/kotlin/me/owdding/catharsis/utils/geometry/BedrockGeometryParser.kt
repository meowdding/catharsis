package me.owdding.catharsis.utils.geometry

import me.owdding.catharsis.generated.CatharsisCodecs
import net.minecraft.util.GsonHelper
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

// Blockbench if display_mode or per face rotations aren't enabled will use 1.12.0 geometry format
// so for now we will just mark this as the only supported version.
private const val SUPPORTED_VERSION = "1.12.0"

object BedrockGeometryParser {

    private val CODEC = CatharsisCodecs.getCodec<BedrockGeometry>()
        .listOf()
        .fieldOf("minecraft:geometry")
        .codec()

    fun parse(geometry: String): List<BedrockGeometry> {
        val json = GsonHelper.parse(geometry)
        val version = GsonHelper.getAsString(json, "format_version")
        if (version != SUPPORTED_VERSION) {
            error("Unsupported Bedrock geometry version: $version")
        }
        return json.toDataOrThrow(CODEC)
    }
}
