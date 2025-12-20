package me.owdding.catharsis.features.blocks.replacements

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.BlendMode
import me.owdding.catharsis.features.blocks.BlockReplacement
import me.owdding.catharsis.features.blocks.BlockReplacementBakery
import me.owdding.catharsis.features.blocks.BlockReplacementEntry
import me.owdding.catharsis.features.blocks.BlockReplacementSelector
import me.owdding.catharsis.features.blocks.BlockSoundDefinition
import me.owdding.catharsis.features.blocks.VirtualBlockStateDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.identifier
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
    override fun <T : Any> bake(
        baker: BlockReplacement.() -> BlockReplacementSelector<T>
    ): BlockReplacementSelector<T> {
        return baker.invoke(this)
    }

    override fun bakeModel(baker: ModelBaker, block: Block): BlockReplacementSelector<BlockReplacementEntry> {
        val override = virtualState.overrides[block]
        return if (override != null) {
            BlockReplacementSelector.always(Baked(virtualState.blend, override.instantiate(block, baker), override.ignoreOriginalOffset))
        } else {
            BlockReplacementSelector.always(Baked(virtualState.blend, virtualState.instantiate(block, baker), virtualState.ignoreOriginalOffset))
        }
    }

    override fun bakeSounds(block: Block): BlockReplacementSelector<BlockSoundDefinition> {
        val override = virtualState.overrides[block]
        return BlockReplacementSelector.always(if (override != null) override.sounds else virtualState.sounds)
    }

    override fun select(state: BlockState, pos: BlockPos, random: RandomSource): VirtualBlockStateDefinition {
        return virtualState.overrides[state.block] ?: virtualState
    }

    data class Baked(
        override val blend: BlendMode?,
        override val models: Map<BlockState, BlockStateModel>,
        override val ignoreOriginalOffset: Boolean,
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
