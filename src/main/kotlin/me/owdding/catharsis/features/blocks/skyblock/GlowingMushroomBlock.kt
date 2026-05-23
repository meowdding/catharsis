package me.owdding.catharsis.features.blocks.skyblock

import me.owdding.catharsis.utils.ParticleCache
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

object GlowingMushroom : SkyBlockBlock {
    override val id = "glowing_mushroom"
    override val vanillaBlocks = listOf(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM)

    override fun check(
        state: BlockState,
        pos: BlockPos,
        level: BlockAndTintGetter,
        random: RandomSource
    ): Boolean {
        if (!SkyBlockIsland.THE_BARN.inIsland()) return false

        return ParticleCache.hasEffectParticle(pos)
    }
}
