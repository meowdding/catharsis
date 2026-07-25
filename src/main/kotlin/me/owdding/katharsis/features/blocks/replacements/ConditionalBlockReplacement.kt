package me.owdding.katharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.blocks.BlockReplacement
import me.owdding.katharsis.features.blocks.BlockReplacementBakery
import me.owdding.katharsis.features.blocks.BlockReplacementSelector
import me.owdding.katharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.katharsis.features.blocks.replacements.conditions.BlockCondition
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState

data class ConditionalBlockReplacement(
    val condition: BlockCondition,
    val definition: BlockReplacement,
    val fallback: BlockReplacement?,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = listOfNotNull(definition.listStates(), fallback?.listStates()).flatten()

    override fun <T : Any> bake(
        baker: BlockReplacement.() -> BlockReplacementSelector<T>
    ): BlockReplacementSelector<T> = ConditionalBlockReplacementSelector(
        condition, definition.baker(), fallback?.baker()
    )

    override fun select(
        level: BlockAndTintGetter?,
        state: BlockState,
        pos: BlockPos,
        random: RandomSource,
    ): VirtualBlockStateDefinition? {
        return when {
            level == null -> null
            condition.check(level.getBlockState(pos), pos, level, random) -> definition
            else -> fallback
        }?.select(level, state, pos, random)
    }

    @GenerateCodec
    @NamedCodec("CompletableConditionalBlockReplacement")
    data class Completable(
        val condition: BlockCondition,
        val definition: BlockReplacement.Completable,
        val fallback: BlockReplacement.Completable?
    ) : BlockReplacement.Completable {
        override fun codec(): MapCodec<Completable> = KatharsisCodecs.getMapCodec()
        override fun virtualStates() = listOfNotNull(definition.virtualStates(), fallback?.virtualStates()).flatten()

        override fun bake(bakery: BlockReplacementBakery) = ConditionalBlockReplacement(
            condition,
            definition.bake(bakery), fallback?.bake(bakery),
        )
    }

    data class ConditionalBlockReplacementSelector<T : Any>(
        val condition: BlockCondition,
        val definition: BlockReplacementSelector<T>,
        val fallback: BlockReplacementSelector<T>?,
    ) : BlockReplacementSelector<T> {
        override fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos, random: RandomSource): T? {
            return when {
                level == null -> null
                condition.check(level.getBlockState(pos), pos, level, random) -> definition
                else -> fallback
            }?.select(level, state, pos, random)
        }
    }
}
