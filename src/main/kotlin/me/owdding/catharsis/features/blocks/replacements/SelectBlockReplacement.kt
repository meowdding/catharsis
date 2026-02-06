package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

data class SelectBlockReplacement(
    val definitions: List<BlockReplacement>,
    val fallback: BlockReplacement?,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = definitions.flatMap { it.listStates() }

    override fun <T : Any> bake(
        baker: BlockReplacement.() -> BlockReplacementSelector<T>
    ): BlockReplacementSelector<T> = SelectBlockReplacementSelector(
        definitions.map { it.bake(baker) }, fallback?.bake(baker),
    )

    override fun select(
        level: BlockAndTintGetter?,
        state: BlockState,
        pos: BlockPos,
        random: RandomSource,
    ): VirtualBlockStateDefinition? {
        return definitions.firstNotNullOfOrNull { it.select(level, state, pos, random) }
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

    data class SelectBlockReplacementSelector<T : Any>(
        val definitions: List<BlockReplacementSelector<T>>,
        val fallback: BlockReplacementSelector<T>?,
    ) : BlockReplacementSelector<T> {
        override fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos, random: RandomSource): T? {
            return definitions.firstNotNullOfOrNull { it.select(level, state, pos, random) }
        }
    }
}
