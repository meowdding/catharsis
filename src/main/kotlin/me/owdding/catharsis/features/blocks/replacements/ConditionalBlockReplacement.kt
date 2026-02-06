package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.features.blocks.replacements.conditions.BlockCondition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
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
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
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
