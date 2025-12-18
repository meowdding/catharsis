package me.owdding.catharsis.features.blocks.replacements

import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementEntry
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.utils.CatharsisLogger
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import kotlin.runCatching

data class LayeredBlockReplacements(
    val definitions: List<BlockReplacement>,
) {
    fun listStates(): List<VirtualBlockStateDefinition> = definitions.flatMap { it.listStates() }
    fun select(state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition? {
        return definitions.firstNotNullOfOrNull { it.select(state, pos, random) }
    }

    data class Completable(
        val definitions: List<BlockReplacement.Completable>,
    ) {
        fun complete(bakery: BlockReplacementBakery, logger: CatharsisLogger): LayeredBlockReplacements = LayeredBlockReplacements(
            definitions.mapNotNull {
                logger.runCatching("Failed to bake block replacement $it") {
                    it.bake(bakery)
                }
            },
        )
    }

    data class LayeredBlockReplacementSelector(
        val blockReplacementSelectors: List<BlockReplacementSelector>,
    ) : BlockReplacementSelector {
        override fun select(
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): BlockReplacementEntry? = blockReplacementSelectors.firstNotNullOfOrNull { it.select(state, pos, random) }
    }

    fun bake(baker: ModelBaker, block: Block): BlockReplacementSelector = LayeredBlockReplacementSelector(definitions.map { it.bake(baker, block) })
}
