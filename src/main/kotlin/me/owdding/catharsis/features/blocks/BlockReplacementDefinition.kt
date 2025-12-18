package me.owdding.catharsis.features.blocks

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.blocks.replacements.*
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.block.Block

interface BlockReplacement {
    interface Completable {
        val codec: MapCodec<out Completable>

        fun virtualStates(): List<ResourceLocation>
        fun bake(bakery: BlockReplacementBakery): BlockReplacement
    }

    fun listStates(): List<VirtualBlockStateDefinition>
    fun bake(baker: ModelBaker, block: Block): BlockReplacementSelector
}

object BlockStateDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out BlockReplacement.Completable>>()

    @IncludedCodec
    val CODEC: MapCodec<BlockReplacement.Completable> = ID_MAPPER.codec(IncludedCodecs.catharsisResourceLocation).dispatchMap(BlockReplacement.Completable::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("redirect"), CatharsisCodecs.getMapCodec<RedirectBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("per_area"), CatharsisCodecs.getMapCodec<PerAreaBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("random"), CatharsisCodecs.getMapCodec<RandomBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("conditional"), CatharsisCodecs.getMapCodec<ConditionalBlockReplacement.Completable>())
    }
}
