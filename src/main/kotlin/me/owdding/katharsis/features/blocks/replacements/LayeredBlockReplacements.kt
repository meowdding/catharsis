package me.owdding.katharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.blocks.BlockReplacement
import me.owdding.katharsis.features.blocks.BlockReplacementBakery
import me.owdding.katharsis.features.blocks.BlockReplacementSelector
import me.owdding.katharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.KatharsisLogger
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
        fun complete(bakery: BlockReplacementBakery, logger: KatharsisLogger): LayeredBlockReplacements = LayeredBlockReplacements(
            definitions.mapNotNull {
                logger.runCatching("Failed to bake block replacement $it") {
                    it.bake(bakery)
                }
            },
        )

        override fun codec(): MapCodec<out BlockReplacement.Completable> = KatharsisCodecs.LayeredBlockReplacementsCodec

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
