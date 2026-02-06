package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

@GenerateCodec
data class NotBlockCondition(val condition: BlockCondition): BlockCondition {

    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<NotBlockCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        return !condition.check(state, pos, level, random)
    }
}

@GenerateCodec
data class AndBlockCondition(val conditions: List<BlockCondition>): BlockCondition {

    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<AndBlockCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        return conditions.all { it.check(state, pos, level, random) }
    }
}

@GenerateCodec
data class OrBlockCondition(val conditions: List<BlockCondition>): BlockCondition {
    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<OrBlockCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        return conditions.any { it.check(state, pos, level, random) }
    }
}
