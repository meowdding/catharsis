package me.owdding.catharsis.features.blocks


import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.Inline
import net.minecraft.client.renderer.block.model.BlockModelDefinition
import net.minecraft.client.renderer.block.model.BlockStateModel
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

@GenerateCodec
data class VirtualBlockStateDefinition(
    @Inline val model: BlockModelDefinition,
    val blend: BlendMode?,
) {

    private var roots: Map<BlockState, BlockStateModel.UnbakedRoot>? = null

    fun getRoots(block: Block): Map<BlockState, BlockStateModel.UnbakedRoot> {
        if (roots == null) {
            roots = model.instantiate(block.stateDefinition) { block.builtInRegistryHolder().key().location().toString() }
        }
        return roots!!
    }

    fun instantiate(block: Block, baker: ModelBaker): Map<BlockState, BlockStateModel> {
        return getRoots(block).mapValues { (state, model) -> model.bake(state, baker) }
    }
}

enum class BlendMode {
    DEFAULT,
    SOLID,
    CUTOUT_MIPPED,
    CUTOUT,
    TRANSLUCENT,
    ;

    fun toSectionLayer(): ChunkSectionLayer? = when (this) {
        DEFAULT -> null
        SOLID -> ChunkSectionLayer.SOLID
        CUTOUT_MIPPED -> ChunkSectionLayer.CUTOUT_MIPPED
        CUTOUT -> ChunkSectionLayer.CUTOUT
        TRANSLUCENT -> ChunkSectionLayer.TRANSLUCENT
    }
}

