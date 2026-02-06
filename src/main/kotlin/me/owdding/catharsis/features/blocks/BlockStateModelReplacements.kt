package me.owdding.catharsis.features.blocks

import me.owdding.catharsis.features.blocks.replacements.LayeredBlockReplacements
import me.owdding.catharsis.utils.extensions.identifier
import me.owdding.catharsis.utils.extensions.mapValuesNotNull
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel
import net.minecraft.client.renderer.block.model.BlockModelPart
import net.minecraft.client.renderer.block.model.BlockStateModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Predicate

fun interface BlockReplacementSelector<T : Any> {
    fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos, random: RandomSource): T?

    companion object {
        fun <T : Any> always(entry: T?): BlockReplacementSelector<T> = BlockReplacementSelector { _, _, _, _ -> entry }
    }
}

interface BlockReplacementEntry {
    val blend: BlendMode?
    val models: Map<BlockState, BlockStateModel>
    val transform: QuadTransform
    val ignoreOriginalOffset: Boolean
}

data class BlockStateModelReplacement(
    val original: BlockStateModel,
    val replacementSelector: BlockReplacementSelector<BlockReplacementEntry>,
    val overrides: Map<Block, BlockReplacementSelector<BlockReplacementEntry>>
) : FabricBlockStateModel by original as FabricBlockStateModel, BlockStateModel {
    override fun emitQuads(emitter: QuadEmitter, level: BlockAndTintGetter, pos: BlockPos, state: BlockState, random: RandomSource, cullTest: Predicate<Direction?>) {
        val replacement = select(level, state, pos)
        val model = replacement?.models[state]

        if (model != null) {
            if (replacement.ignoreOriginalOffset) {
                val originalOffset = state.getOffset(pos)
                emitter.pushTransform {
                    for (i in 0..3) {
                        it.pos(
                            i,
                            it.posByIndex(i, 0) - originalOffset.x.toFloat(),
                            it.posByIndex(i, 1) - originalOffset.y.toFloat(),
                            it.posByIndex(i, 2) - originalOffset.z.toFloat()
                        )
                    }
                    true
                }
            }
            emitter.pushTransform(replacement.transform)
            model.emitQuads(emitter, level, pos, state, random, cullTest)
            emitter.popTransform()

            if (replacement.ignoreOriginalOffset) {
                emitter.popTransform()
            }
            return
        }
        super<BlockStateModel>.emitQuads(emitter, level, pos, state, random, cullTest)
    }

    override fun collectParts(random: RandomSource, output: List<BlockModelPart>) {
        original.collectParts(random, output)
    }

    override fun particleSprite(level: BlockAndTintGetter, pos: BlockPos, state: BlockState): TextureAtlasSprite? {
        val replacement = select(level, state, pos)
        val model = replacement?.models[state]
        if (model != null) {
            return model.particleSprite(level, pos, state)
        }
        return super<FabricBlockStateModel>.particleSprite(level, pos, state)
    }

    override fun particleIcon(): TextureAtlasSprite? {
        return original.particleIcon()
    }

    fun select(level: BlockAndTintGetter?, state: BlockState, pos: BlockPos): BlockReplacementEntry? {
        val random = RandomSource.create(Mth.getSeed(pos))

        val cacheState = BlockReplacements.blocksCache.getIfPresent(pos)
        val override = overrides[cacheState?.block]
        return replacementSelector.select(level, state, pos, random) ?: cacheState?.let { override?.select(level, cacheState, pos, random) }
    }
}

data class UnbakedBlockStateModelReplacement(
    val block: Block,
    val original: BlockStateModel.UnbakedRoot,
    val entries: LayeredBlockReplacements,
    val overrides: Map<Block, LayeredBlockReplacements>
) : BlockStateModel.UnbakedRoot {
    override fun bake(
        state: BlockState,
        baker: ModelBaker,
    ): BlockStateModel = BlockStateModelReplacement(
        original.bake(state, baker),
        entries.bake { bakeModel(baker, state.block) },
        overrides.mapValuesNotNull { it.value.bake { bakeModel(baker, state.block) } }
    )

    override fun visualEqualityGroup(state: BlockState): Any? = original.visualEqualityGroup(state)

    override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
        original.resolveDependencies(resolver)
        entries.listStates().forEach {
            it.getRoots(block).values.forEach { root ->
                root.resolveDependencies(resolver)
            }
        }
        overrides.flatMap { it.value.listStates() }.forEach {
            it.overrides[block]?.model?.instantiate(block.stateDefinition) {
                block.identifier.toString()
            }?.values?.forEach { root ->
                root.resolveDependencies(resolver)
            }
        }
    }

}
