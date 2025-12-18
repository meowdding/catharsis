package me.owdding.catharsis.features.blocks

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
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
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Predicate

fun interface BlockReplacementSelector {
    fun select(state: BlockState, pos: BlockPos, random: RandomSource): BlockReplacementEntry?

    companion object {
        fun always(entry: BlockReplacementEntry): BlockReplacementSelector = BlockReplacementSelector { _, _, _ -> entry }
    }
}

interface BlockReplacementEntry {
    val blend: BlendMode?
    val models: Map<BlockState, BlockStateModel>
    val transform: QuadTransform
}

data class BlockStateModelReplacement(
    val original: BlockStateModel,
    val replacementSelector: BlockReplacementSelector,
): FabricBlockStateModel by original as FabricBlockStateModel, BlockStateModel {
    override fun emitQuads(emitter: QuadEmitter, blockView: BlockAndTintGetter, pos: BlockPos, state: BlockState, random: RandomSource, cullTest: Predicate<Direction?>) {
        val random = RandomSource.create(Mth.getSeed(pos))
        val replacement = replacementSelector.select(state, pos, random)
        val model = replacement?.models[state]
        if (model != null) {
            emitter.pushTransform(replacement.transform)
            model.emitQuads(emitter, blockView, pos, state, random, cullTest)
            emitter.popTransform()
            return
        }

        super<BlockStateModel>.emitQuads(emitter, blockView, pos, state, random, cullTest)
    }

    override fun collectParts(random: RandomSource, output: List<BlockModelPart>) {
        original.collectParts(random, output)
    }

    override fun particleSprite(blockView: BlockAndTintGetter, pos: BlockPos, state: BlockState): TextureAtlasSprite? {
        val random = RandomSource.create(Mth.getSeed(pos))
        val replacement = replacementSelector.select(state, pos, random)
        val model = replacement?.models[state]
        if (model != null) {
            return model.particleSprite(blockView, pos, state)
        }
        return super<FabricBlockStateModel>.particleSprite(blockView, pos, state)
    }

    override fun particleIcon(): TextureAtlasSprite? {
        return original.particleIcon()
    }
}

data class UnbakedBlockStateModelReplacement(
    val block: Block,
    val original: BlockStateModel.UnbakedRoot,
    val entries: LayeredBlockReplacements,
) : BlockStateModel.UnbakedRoot {
    override fun bake(
        state: BlockState,
        baker: ModelBaker,
    ): BlockStateModel = BlockStateModelReplacement(
        original.bake(state, baker),
        entries.bake(baker, state.block)
    )

    override fun visualEqualityGroup(state: BlockState): Any? = original.visualEqualityGroup(state)

    override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
        original.resolveDependencies(resolver)
        entries.listStates().forEach {
            it.getRoots(block).values.forEach { root ->
                root.resolveDependencies(resolver)
            }
        }
    }

}

@GenerateCodec
data class BlockSoundDefinition(
    // https://github.com/meowdding/minecwaft-sources/blob/618a50f16586c10c2607d126a8d559cef9a0b2c9/src/main/java/net/minecraft/client/multiplayer/MultiPlayerGameMode.java#L254
    @FieldName("hit") val hitSound: SoundEvent?,
    // https://github.com/meowdding/minecwaft-sources/blob/618a50f16586c10c2607d126a8d559cef9a0b2c9/src/main/java/net/minecraft/client/renderer/LevelEventHandler.java#L438
    @FieldName("break") val breakSound: SoundEvent?,
    // https://github.com/meowdding/minecwaft-sources/blob/618a50f16586c10c2607d126a8d559cef9a0b2c9/src/main/java/net/minecraft/world/entity/Entity.java#L1329
    @FieldName("step") val stepSound: SoundEvent?,
    // https://github.com/meowdding/minecwaft-sources/blob/618a50f16586c10c2607d126a8d559cef9a0b2c9/src/main/java/net/minecraft/world/item/BlockItem.java#L98
    @FieldName("place") val placeSound: SoundEvent?,
    // https://github.com/meowdding/minecwaft-sources/blob/618a50f16586c10c2607d126a8d559cef9a0b2c9/src/main/java/net/minecraft/world/entity/LivingEntity.java#L1800
    // needs custom impl in honey block bc fuck you minecraft but who cares
    @FieldName("fall") val fallSound: SoundEvent?,
) {
    val soundType = SoundType(1f, 1f, breakSound, stepSound, placeSound, hitSound, fallSound)

    companion object {
        val DEFAULT = BlockSoundDefinition(null, null, null, null, null)
    }
}
