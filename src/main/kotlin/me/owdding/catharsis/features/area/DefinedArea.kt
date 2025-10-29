package me.owdding.catharsis.features.area

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.boundingboxes.BoundingBox
import me.owdding.catharsis.utils.boundingboxes.DebugRenderable
import me.owdding.catharsis.utils.boundingboxes.Octree
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.*
import me.owdding.ktcodecs.IntRange
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

interface AreaDefinition {
    fun codec(): MapCodec<out AreaDefinition>
    fun contains(blockPos: BlockPos): Boolean
    val renderable: DebugRenderable?

    fun <T : Any> T.checkIslands(islands: List<SkyBlockIsland>?) = this.takeIf { islands?.contains(LocationAPI.island) != false }
}

@GenerateCodec
data class SimpleAreaDefinition(
    @Compact val islands: List<SkyBlockIsland>?,
    val box: BoundingBox,
) : AreaDefinition {
    override fun codec(): MapCodec<SimpleAreaDefinition> = CatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos) = box.checkIslands(islands)?.contains(blockPos) == true
    override val renderable: DebugRenderable? get() = box.checkIslands(islands)
}

@GenerateCodec
data class OnIslandDefinition(
    @Compact val islands: List<SkyBlockIsland>,
) : AreaDefinition {
    override fun codec(): MapCodec<OnIslandDefinition> = CatharsisCodecs.getMapCodec()
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
    @Compact val islands: List<SkyBlockIsland>?,
    @Compact val boxes: List<BoundingBox>,
    @IntRange(4) val minSize: Int = 8,
) : AreaDefinition {
    val tree: Octree? = Octree(boxes, minSize)
        get() = field?.checkIslands(islands)
    override val renderable: DebugRenderable? get() = tree

    override fun codec(): MapCodec<MultipleAreaDefinition> = CatharsisCodecs.getMapCodec()
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
    override fun codec(): MapCodec<MultipleAreaDefinition> = CatharsisCodecs.getMapCodec()
    override fun contains(blockPos: BlockPos): Boolean = islands[LocationAPI.island]?.contains(blockPos) == true
}

@GenerateCodec
data class IslandEntry(
    @Compact val islands: List<SkyBlockIsland>,
    @Inline val definition: AreaDefinition,
)

object AreaDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out AreaDefinition>>()

    @IncludedCodec
    val CODEC: MapCodec<AreaDefinition> = ID_MAPPER.codec(IncludedCodecs.catharsisResourceLocation).dispatchMap(AreaDefinition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("multiple"), CatharsisCodecs.getMapCodec<MultipleAreaDefinition>())
        ID_MAPPER.put(Catharsis.id("per_island"), CatharsisCodecs.getMapCodec<PerIslandAreaDefinition>())
        ID_MAPPER.put(Catharsis.id("simple"), CatharsisCodecs.getMapCodec<SimpleAreaDefinition>())
        ID_MAPPER.put(Catharsis.id("on_island"), CatharsisCodecs.getMapCodec<OnIslandDefinition>())
        ID_MAPPER.put(Catharsis.id("always"), AlwaysTrueDefinition.codec())
    }

}
