package me.owdding.catharsis.features.area

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.boundingboxes.BoundingBox
import me.owdding.catharsis.utils.boundingboxes.Octree
import me.owdding.ktcodecs.*
import me.owdding.ktcodecs.IntRange
import net.minecraft.core.BlockPos
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

interface AreaDefinition {
    fun codec(): MapCodec<out AreaDefinition>
    fun contains(blockPos: BlockPos): Boolean
    val tree: Octree?
}

@GenerateCodec
data class SimpleAreaDefinition(
    @Compact val islands: List<SkyBlockIsland>?,
    @Compact val boxes: List<BoundingBox>,
    @IntRange(4) val minSize: Int = 8
) : AreaDefinition {
    override val tree = Octree(boxes, minSize)
    override fun codec(): MapCodec<SimpleAreaDefinition> = CatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos) = tree.takeIf { islands?.contains(LocationAPI.island) != false }?.contains(blockPos) == true
}

@GenerateCodec
data class PerIslandAreaDefinition(
    val entries: List<IslandEntry>
) : AreaDefinition {
    @Suppress("SENSELESS_COMPARISON")
    val islands = buildMap<SkyBlockIsland, AreaDefinition> {
        this@PerIslandAreaDefinition.entries.forEach { (islands, definition) ->
            islands.forEach { island ->
                if (this.containsKey(island) || island == null) throw UnsupportedOperationException("Duplicate island $island!")
                put(island, definition)
            }
        }
    }
    override val tree: Octree? get() = islands[LocationAPI.island]?.tree
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
    val CODEC: MapCodec<AreaDefinition> = ID_MAPPER.codec(Codec.STRING).dispatchMap(AreaDefinition::codec) { it }

    init {
        ID_MAPPER.put("basic", CatharsisCodecs.getMapCodec<SimpleAreaDefinition>())
        ID_MAPPER.put("per_island", CatharsisCodecs.getMapCodec<PerIslandAreaDefinition>())
    }

}
