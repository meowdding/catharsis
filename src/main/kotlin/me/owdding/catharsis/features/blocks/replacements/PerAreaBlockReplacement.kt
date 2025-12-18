package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.area.Areas
import me.owdding.catharsis.features.blocks.*
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

data class PerAreaBlockReplacement(
    val values: Map<Identifier, BlockReplacement>,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = values.values.flatMap { it.listStates() }
    override fun select(state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition? {
        return values.firstNotNullOfOrNull { (area, value) ->
            value.takeIf { Areas.getLoadedAreas()[area]?.contains(pos) == true }?.select(state, pos, random)
        }
    }

    data class PerAreaBlockReplacementSelector(
        val values: Map<Identifier, BlockReplacementSelector>,
    ) : BlockReplacementSelector {
        override fun select(
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): BlockReplacementEntry? {
            return values.firstNotNullOfOrNull { (area, value) ->
                value.takeIf { Areas.getLoadedAreas()[area]?.contains(pos) == true }?.select(state, pos, random)
            }
        }

    }

    override fun bake(
        baker: ModelBaker,
        block: Block,
    ): BlockReplacementSelector = PerAreaBlockReplacementSelector(values.mapValues { (_, value) -> value.bake(baker, block) })

    @GenerateCodec
    @NamedCodec("CompletablePerAreaBlockReplacement")
    data class Completable(
        @FieldName("entries") val values: Map<Identifier, BlockReplacement.Completable>,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = values.values.flatMap { it.virtualStates() }
        override fun bake(bakery: BlockReplacementBakery): BlockReplacement = PerAreaBlockReplacement(values.mapValues { it.value.bake(bakery) })
    }
}
