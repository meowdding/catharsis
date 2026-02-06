package me.owdding.catharsis.features.blocks.replacements

import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.utils.CatharsisLogger
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

data class LayeredBlockReplacements(
    val definitions: List<BlockReplacement>,
) {
    fun listStates(): List<VirtualBlockStateDefinition> = definitions.flatMap { it.listStates() }
    fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition? {
        return definitions.firstNotNullOfOrNull { it.select(level, state, pos, random) }
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

    data class LayeredBlockReplacementSelector<T : Any>(
        val blockReplacementSelectors: List<BlockReplacementSelector<T>>,
    ) : BlockReplacementSelector<T> {
        override fun select(
            level: BlockAndTintGetter?,
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): T? = blockReplacementSelectors.firstNotNullOfOrNull { it.select(level, state, pos, random) }
    }

    fun <T : Any> bake(baker: BlockReplacement.() -> BlockReplacementSelector<T>): BlockReplacementSelector<T> = LayeredBlockReplacementSelector(definitions.map { it.bake(baker) })
    fun bakeModel(baker: ModelBaker, block: Block) = bake { bakeModel(baker, block) }
    fun bakeSounds(block: Block) = bake { bakeSounds(block) }

}
