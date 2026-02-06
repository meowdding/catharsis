package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.area.Areas
import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

data class PerAreaBlockReplacement(
    val values: Map<Identifier, BlockReplacement>,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = values.values.flatMap { it.listStates() }
    override fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition? {
        return values.firstNotNullOfOrNull { (area, value) ->
            value.takeIf { Areas.getLoadedAreas()[area]?.contains(pos) == true }?.select(level, state, pos, random)
        }
    }

    data class PerAreaBlockReplacementSelector<T: Any>(
        val values: Map<Identifier, BlockReplacementSelector<T>>,
    ) : BlockReplacementSelector<T> {
        override fun select(
            level: BlockAndTintGetter?,
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): T? {
            return values.firstNotNullOfOrNull { (area, value) ->
                value.takeIf { Areas.getLoadedAreas()[area]?.contains(pos) == true }?.select(level, state, pos, random)
            }
        }

    }

    override fun <T: Any> bake(
        baker: BlockReplacement.() -> BlockReplacementSelector<T>
    ): BlockReplacementSelector<T> = PerAreaBlockReplacementSelector(values.mapValues { (_, value) -> value.bake(baker) })

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
