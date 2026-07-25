package me.owdding.katharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.extensions.plus
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3ic

@GenerateCodec
data class RelativeCondition(
    val offset: Vector3ic,
    val condition: BlockCondition,
): BlockCondition {
    override val codec: MapCodec<out BlockCondition> = KatharsisCodecs.getMapCodec<RelativeCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        val newPos = pos + offset
        return condition.check(level.getBlockState(newPos), newPos, level, random)
    }
}
