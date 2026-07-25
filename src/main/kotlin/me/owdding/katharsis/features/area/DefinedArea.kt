package me.owdding.katharsis.features.area

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.katharsis.utils.codecs.SavableData
import me.owdding.katharsis.utils.types.boundingboxes.BoundingBox
import me.owdding.katharsis.utils.types.boundingboxes.DebugRenderable
import me.owdding.katharsis.utils.types.boundingboxes.Octree
import me.owdding.ktcodecs.*
import me.owdding.ktcodecs.IntRange
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

interface AreaDefinition : SavableData<AreaDefinition> {
    override val codec: Codec<AreaDefinition> get() = Areas.codec
    override fun toFileName(identifier: Identifier): Identifier = Areas.converter.idToFile(identifier)

    fun codec(): MapCodec<out AreaDefinition>
    fun contains(blockPos: BlockPos): Boolean
    val renderable: DebugRenderable?

    fun <T : Any> T.checkIslands(islands: List<SkyBlockIsland>?) = this.takeIf { islands?.contains(LocationAPI.island) != false }
}

@GenerateCodec
data class SimpleAreaDefinition(
    @Compact @FieldNames("island", "islands") val islands: List<SkyBlockIsland>?,
    val box: BoundingBox,
) : AreaDefinition {
    override fun codec(): MapCodec<SimpleAreaDefinition> = KatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos) = box.checkIslands(islands)?.contains(blockPos) == true
    override val renderable: DebugRenderable? get() = box.checkIslands(islands)
}

@GenerateCodec
data class OnIslandDefinition(
    @Compact @FieldNames("island", "islands") val islands: List<SkyBlockIsland>,
) : AreaDefinition {
    override fun codec(): MapCodec<OnIslandDefinition> = KatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos) = true.checkIslands(islands) == true
    override val renderable: DebugRenderable? = null
}

object AlwaysTrueDefinition : AreaDefinition {
    override fun codec(): MapCodec<AlwaysTrueDefinition> = MapCodec.unit(AlwaysTrueDefinition)
    override fun contains(blockPos: BlockPos): Boolean = true
    override val renderable: DebugRenderable? = null
}

@GenerateCodec
data class MultipleAreaDefinition(
    @Compact @FieldNames("island", "islands") val islands: List<SkyBlockIsland>?,
    @Compact val boxes: List<BoundingBox>,
    @IntRange(4) val minSize: Int = 8,
) : AreaDefinition {
    val tree: Octree? = Octree(boxes, minSize)
        get() = field?.checkIslands(islands)
    override val renderable: DebugRenderable? get() = tree

    override fun codec(): MapCodec<MultipleAreaDefinition> = KatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos) = tree?.contains(blockPos) == true
}

@GenerateCodec
data class PerIslandAreaDefinition(
    val entries: List<IslandEntry>,
) : AreaDefinition {
    val islands = buildMap<SkyBlockIsland, AreaDefinition> {
        this@PerIslandAreaDefinition.entries.forEach { (islands, definition) ->
            islands.forEach { island ->
                if (this.containsKey(island)) throw UnsupportedOperationException("Duplicate island $island!")
                put(island, definition)
            }
        }
    }
    override val renderable: DebugRenderable? get() = islands[LocationAPI.island]?.renderable
    override fun codec(): MapCodec<MultipleAreaDefinition> = KatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos): Boolean = islands[LocationAPI.island]?.contains(blockPos) == true
}

@GenerateCodec
data class IslandEntry(
    @Compact @FieldNames("island", "islands") val islands: List<SkyBlockIsland>,
    @Inline val definition: AreaDefinition,
)

object AreaDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out AreaDefinition>>()

    @IncludedCodec
    val CODEC: MapCodec<AreaDefinition> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier).dispatchMap(AreaDefinition::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("multiple"), KatharsisCodecs.getMapCodec<MultipleAreaDefinition>())
        ID_MAPPER.put(Katharsis.id("per_island"), KatharsisCodecs.getMapCodec<PerIslandAreaDefinition>())
        ID_MAPPER.put(Katharsis.id("simple"), KatharsisCodecs.getMapCodec<SimpleAreaDefinition>())
        ID_MAPPER.put(Katharsis.id("on_island"), KatharsisCodecs.getMapCodec<OnIslandDefinition>())
        ID_MAPPER.put(Katharsis.id("always"), AlwaysTrueDefinition.codec())
    }

}
