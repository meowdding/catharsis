package me.owdding.catharsis.features.area

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.boundingboxes.BoundingBox
import me.owdding.catharsis.utils.boundingboxes.Octree
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.Unnamed
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

data class DefinedArea(
    val id: ResourceLocation,
    val definition: AreaDefinition,
)

interface AreaDefinition {
    fun codec(): MapCodec<out AreaDefinition>
    fun contains(blockPos: BlockPos): Boolean
}

@GenerateCodec
data class SimpleAreaDefinition(
    @Compact val islands: SkyBlockIsland,
    @Compact val boxes: List<BoundingBox>,
) : AreaDefinition {
    val tree = Octree(boxes)
    override fun codec(): MapCodec<SimpleAreaDefinition> = CatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos) = tree.contains(blockPos)
}

@GenerateCodec
data class PerIslandAreaDefinition(
    val entries: List<IslandEntry>
) : AreaDefinition {
    val islands = buildMap<SkyBlockIsland, AreaDefinition> {
        this@PerIslandAreaDefinition.entries.forEach { (islands, definition) ->
            islands.forEach { island ->
                if (this.containsKey(island)) throw UnsupportedOperationException("Duplicate island $island!")
                put(island, definition)
            }
        }
    }
    override fun codec(): MapCodec<SimpleAreaDefinition> = CatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos): Boolean = islands[LocationAPI.island]?.contains(blockPos) == true
}

@GenerateCodec
data class IslandEntry(
    @Compact val islands: List<SkyBlockIsland>,
    @Unnamed val definition: AreaDefinition
)

object AreaDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out AreaDefinition>>()
    @IncludedCodec
    val CODEC: Codec<AreaDefinition> = ID_MAPPER.codec(Codec.STRING).dispatch(AreaDefinition::codec) { it }

    init {
        ID_MAPPER.put("basic", CatharsisCodecs.getMapCodec<SimpleAreaDefinition>())
        ID_MAPPER.put("per_island", CatharsisCodecs.getMapCodec<SimpleAreaDefinition>())
    }

}
