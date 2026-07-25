package me.owdding.katharsis.features.blocks


import com.mojang.serialization.Codec
import me.owdding.katharsis.utils.codecs.SavableData
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.Inline
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher
import net.minecraft.client.renderer.block.dispatch.BlockStateModel
import net.minecraft.client.renderer.chunk.ChunkSectionLayer
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.platform.identifier

@GenerateCodec
data class VirtualBlockStateDefinition(
    @Inline val model: BlockStateModelDispatcher,
    val sounds: BlockSoundDefinition?,
    val display: BlockDisplayDefinition?,
    val blend: BlendMode?,
    @FieldName("ignore_original_offset") val ignoreOriginalOffset: Boolean = false,
    val overrides: Map<Block, VirtualBlockStateDefinition> = emptyMap(),
) : SavableData<VirtualBlockStateDefinition> {
    override val codec: Codec<VirtualBlockStateDefinition> get() = BlockReplacements.virtualBlockStateCodec
    override fun toFileName(identifier: Identifier): Identifier = BlockReplacements.blockStateConverter.idToFile(identifier)

    private var roots: Map<BlockState, BlockStateModel.UnbakedRoot>? = null

    fun getRoots(block: Block): Map<BlockState, BlockStateModel.UnbakedRoot> {
        if (roots == null) {
            roots = model.instantiate(block.stateDefinition) { block.builtInRegistryHolder().key().identifier.toString() }
        }
        return roots!!
    }

    fun instantiate(block: Block, baker: ModelBaker): Map<BlockState, BlockStateModel> {
        return getRoots(block).mapValues { (state, model) -> model.bake(state, baker) }
    }
}

enum class BlendMode(val sectionLayer: ChunkSectionLayer) {
    DEFAULT(ChunkSectionLayer.CUTOUT),
    SOLID(ChunkSectionLayer.SOLID),
    CUTOUT(ChunkSectionLayer.CUTOUT),
    TRANSLUCENT(ChunkSectionLayer.TRANSLUCENT),
}

