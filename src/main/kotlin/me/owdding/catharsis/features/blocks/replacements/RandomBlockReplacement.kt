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
import net.minecraft.world.level.block.state.BlockState

data class RandomBlockReplacement(
    val min: Float,
    val max: Float,
    val threshold: Float,
    val definition: BlockReplacement,
    val fallback: BlockReplacement?,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = listOfNotNull(definition.listStates(), fallback?.listStates()).flatten()
    override fun select(state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition? {
        return if (min + random.nextFloat() * (max - min) >= threshold) {
            definition
        } else {
            fallback
        }?.select(state, pos, random)
    }

    data class RandomBlockReplacementSelector<T: Any>(
        val min: Float, val max: Float, val threshold: Float,
        val definition: BlockReplacementSelector<T>,
        val fallback: BlockReplacementSelector<T>?,
    ) : BlockReplacementSelector<T> {
        override fun select(
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): T? {
            return if (min + random.nextFloat() * (max - min) >= threshold) {
                definition
            } else {
                fallback
            }?.select(state, pos, random)
        }
    }

    override fun <T: Any> bake(
        baker: BlockReplacement.() -> BlockReplacementSelector<T>
    ): BlockReplacementSelector<T> = RandomBlockReplacementSelector(min, max, threshold, definition.bake(baker), fallback?.bake(baker))


    @GenerateCodec
    @NamedCodec("CompletableRandomBlockReplacement")
    data class Completable(
        val min: Float,
        val max: Float,
        val threshold: Float,
        val definition: BlockReplacement.Completable,
        val fallback: BlockReplacement.Completable?,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = listOfNotNull(definition.virtualStates(), fallback?.virtualStates()).flatten()
        override fun bake(bakery: BlockReplacementBakery) = RandomBlockReplacement(
            min, max, threshold,
            definition.bake(bakery), fallback?.bake(bakery),
        )
    }
}
