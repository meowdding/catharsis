package me.owdding.catharsis.features.blocks

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.blocks.replacements.*
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

interface BlockReplacement {
    interface Completable {
        val codec: MapCodec<out Completable>

        fun virtualStates(): List<Identifier>
        fun bake(bakery: BlockReplacementBakery): BlockReplacement
    }

    fun listStates(): List<VirtualBlockStateDefinition>
    fun <T : Any> bake(baker: BlockReplacement.() -> BlockReplacementSelector<T>): BlockReplacementSelector<T>
    fun bakeModel(baker: ModelBaker, block: Block): BlockReplacementSelector<BlockReplacementEntry> = bake { bakeModel(baker, block) }
    fun bakeSounds(block: Block): BlockReplacementSelector<BlockSoundDefinition> = bake { bakeSounds(block) }

    fun select(
        state: BlockState,
        pos: BlockPos,
        random: RandomSource,
    ): VirtualBlockStateDefinition?
}

object BlockStateDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out BlockReplacement.Completable>>()

    @IncludedCodec
    val CODEC: MapCodec<BlockReplacement.Completable> = ID_MAPPER.codec(IncludedCodecs.catharsisIdentifier).dispatchMap(BlockReplacement.Completable::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("select"), CatharsisCodecs.getMapCodec<SelectBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("redirect"), CatharsisCodecs.getMapCodec<RedirectBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("per_area"), CatharsisCodecs.getMapCodec<PerAreaBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("random"), CatharsisCodecs.getMapCodec<RandomBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("conditional"), CatharsisCodecs.getMapCodec<ConditionalBlockReplacement.Completable>())
    }
}
