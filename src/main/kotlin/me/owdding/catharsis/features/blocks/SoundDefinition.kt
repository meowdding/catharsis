package me.owdding.catharsis.features.blocks

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.helpers.McLevel


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


data class BakedSoundDefinition(
    val entries: BlockReplacementSelector<BlockSoundDefinition>,
    val overrides: Map<Block, BlockReplacementSelector<BlockSoundDefinition>>
) {

    fun select(state: BlockState, pos: BlockPos): BlockSoundDefinition? {
        val random = RandomSource.create(Mth.getSeed(pos))

        val cacheState = BlockReplacements.blocksCache.getIfPresent(pos)
        val override = overrides[cacheState?.block]
        return entries.select(McLevel.selfOrNull, state, pos, random) ?: cacheState?.let { override?.select(McLevel.selfOrNull, cacheState, pos, random) }
    }
}
