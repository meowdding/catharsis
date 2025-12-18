package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.*
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform
import net.minecraft.client.renderer.block.model.BlockStateModel
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

data class RedirectBlockReplacement(
    val virtualState: VirtualBlockStateDefinition,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = listOf(virtualState)
    override fun bake(baker: ModelBaker, block: Block) = BlockReplacementSelector.always(Baked(virtualState.blend, virtualState.instantiate(block, baker)))
    override fun select(state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition = virtualState

    data class Baked(
        override val blend: BlendMode?,
        override val models: Map<BlockState, BlockStateModel>,
    ) : BlockReplacementEntry {
        override val transform: QuadTransform by lazy {
            if (blend != null) {
                QuadTransform { quad ->
                    quad.renderLayer(blend.sectionLayer)
                    true
                }
            } else {
                QuadTransform { true }
            }
        }
    }

    @GenerateCodec
    @NamedCodec("CompletableRedirectBlockReplacement")
    data class Completable(
        @FieldName("virtual_state") val virtualState: Identifier,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = listOf(virtualState)
        override fun bake(bakery: BlockReplacementBakery): BlockReplacement = RedirectBlockReplacement(bakery.virtualStates[virtualState]!!.copy())
    }
}
