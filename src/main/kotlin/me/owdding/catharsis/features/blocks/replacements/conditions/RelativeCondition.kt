package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.plus
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3ic

@GenerateCodec
data class RelativeCondition(
    val offset: Vector3ic,
    val condition: BlockCondition,
): BlockCondition {
    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<RelativeCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        val newPos = pos + offset
        return condition.check(level.getBlockState(newPos), newPos, level, random)
    }
}
