package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

@GenerateCodec
data class InIslandCondition(
    val island: SkyBlockIsland,
): BlockCondition {
    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<InIslandCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        return island.inIsland()
    }
}
