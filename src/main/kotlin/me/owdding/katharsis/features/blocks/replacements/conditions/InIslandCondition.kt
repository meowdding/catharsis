package me.owdding.katharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

@GenerateCodec
data class InIslandCondition(
    val island: SkyBlockIsland,
): BlockCondition {
    override val codec: MapCodec<out BlockCondition> = KatharsisCodecs.getMapCodec<InIslandCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        return island.inIsland()
    }
}
