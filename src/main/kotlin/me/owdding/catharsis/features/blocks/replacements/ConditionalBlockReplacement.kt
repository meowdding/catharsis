package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.*
import me.owdding.catharsis.features.blocks.replacements.conditions.BlockCondition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.helpers.McLevel

data class ConditionalBlockReplacement(
    val condition: BlockCondition,
    val definition: BlockReplacement,
    val fallback: BlockReplacement?,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = listOfNotNull(definition.listStates(), fallback?.listStates()).flatten()

    override fun bake(
        baker: ModelBaker,
        block: Block,
    ): BlockReplacementSelector = ConditionalBlockReplacementSelector(
        condition, definition.bake(baker, block), fallback?.bake(baker, block)
    )

    override fun select(
        state: BlockState,
        pos: BlockPos,
        random: RandomSource,
    ): VirtualBlockStateDefinition? {
        return when {
            !McLevel.hasLevel -> null
            condition.check(McLevel[pos], pos, McLevel.level, random) -> definition
            else -> fallback
        }?.select(state, pos, random)
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

    data class ConditionalBlockReplacementSelector(
        val condition: BlockCondition,
        val definition: BlockReplacementSelector,
        val fallback: BlockReplacementSelector?,
    ) : BlockReplacementSelector {
        override fun select(state: BlockState, pos: BlockPos, random: RandomSource): BlockReplacementEntry? {
            return when {
                !McLevel.hasLevel -> null
                condition.check(McLevel[pos], pos, McLevel.level, random) -> definition
                else -> fallback
            }?.select(state, pos, random)
        }
    }
}
