package me.owdding.catharsis.features.blocks.skyblock

import me.owdding.catharsis.utils.CustomBlockProvider
import me.owdding.catharsis.utils.ParticleCache
import me.owdding.catharsis.utils.ParticleInvalidateable
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McLevel

@ParticleInvalidateable
object GlowingMushroom : SkyBlockBlock, CustomBlockProvider {
    override val id = "glowing_mushroom"
    override val vanillaBlocks = listOf(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM)

    override fun check(
        state: BlockState,
        pos: BlockPos,
        level: BlockAndTintGetter,
        random: RandomSource
    ): Boolean {
        if (!SkyBlockIsland.THE_BARN.inIsland()) return false
        if (state.`is`(Blocks.BROWN_MUSHROOM) || state.`is`(Blocks.RED_MUSHROOM)) {
            return ParticleCache.hasEffectParticle(pos.atY(0))
        }
        return false
    }

    override fun isCustomBlock(pos: BlockPos): Boolean {
        return vanillaBlocks.contains(McLevel[pos].block)
    }
}
