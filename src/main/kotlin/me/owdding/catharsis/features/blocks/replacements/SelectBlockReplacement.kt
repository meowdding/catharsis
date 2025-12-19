package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.*
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

data class SelectBlockReplacement(
    val definitions: List<BlockReplacement>,
    val fallback: BlockReplacement?,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = definitions.flatMap { it.listStates() }

    override fun bake(
        baker: ModelBaker,
        block: Block,
    ): BlockReplacementSelector = SelectBlockReplacementSelector(
        definitions.map { it.bake(baker, block) }, fallback?.bake(baker, block),
    )

    override fun select(
        state: BlockState,
        pos: BlockPos,
        random: RandomSource,
    ): VirtualBlockStateDefinition? {
        return definitions.firstNotNullOfOrNull { it.select(state, pos, random) }
    }

    @GenerateCodec
    @NamedCodec("CompletableSelectBlockReplacement")
    data class Completable(
        val definitions: List<BlockReplacement.Completable>,
        val fallback: BlockReplacement.Completable?,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = listOfNotNull(definitions.flatMap { it.virtualStates() }, fallback?.virtualStates()).flatten()

        override fun bake(bakery: BlockReplacementBakery) = SelectBlockReplacement(
            definitions.map { it.bake(bakery) }, fallback?.bake(bakery),
        )
    }

    data class SelectBlockReplacementSelector(
        val definitions: List<BlockReplacementSelector>,
        val fallback: BlockReplacementSelector?,
    ) : BlockReplacementSelector {
        override fun select(state: BlockState, pos: BlockPos, random: RandomSource): BlockReplacementEntry? {
            return definitions.firstNotNullOfOrNull { it.select(state, pos, random) }
        }
    }
}
