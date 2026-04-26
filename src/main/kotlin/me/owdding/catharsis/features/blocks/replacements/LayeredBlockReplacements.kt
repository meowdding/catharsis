package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState


data class LayeredBlockReplacements(
    val definitions: List<BlockReplacement>,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = definitions.flatMap { it.listStates() }
    override fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition? {
        return definitions.firstNotNullOfOrNull { it.select(level, state, pos, random) }
    }

    @GenerateCodec
    @NamedCodec("LayeredBlockReplacements")
    data class Completable(
        val definitions: List<BlockReplacement.Completable>,
    ) : BlockReplacement.Completable {
        fun complete(bakery: BlockReplacementBakery, logger: CatharsisLogger): LayeredBlockReplacements = LayeredBlockReplacements(
            definitions.mapNotNull {
                logger.runCatching("Failed to bake block replacement $it") {
                    it.bake(bakery)
                }
            },
        )

        override fun codec(): MapCodec<out BlockReplacement.Completable> = CatharsisCodecs.LayeredBlockReplacementsCodec

        override fun virtualStates(): List<Identifier> = definitions.flatMap { it.virtualStates() }

        override fun bake(bakery: BlockReplacementBakery): BlockReplacement = LayeredBlockReplacements(definitions.map { it.bake(bakery) })
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

    override fun <T : Any> bake(baker: BlockReplacement.() -> BlockReplacementSelector<T>): BlockReplacementSelector<T> = LayeredBlockReplacementSelector(definitions.map { it.bake(baker) })

}
