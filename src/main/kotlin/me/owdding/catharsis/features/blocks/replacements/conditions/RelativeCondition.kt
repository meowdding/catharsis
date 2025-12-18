package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.add
import me.owdding.catharsis.utils.extensions.plus
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import org.joml.Vector3ic

@GenerateCodec
data class RelativeCondition(
    val offset: Vector3ic,
    val condition: BlockCondition,
): BlockCondition {
    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<RelativeCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: Level, random: RandomSource): Boolean {
        val newPos = pos + offset
        return condition.check(level.getBlockState(newPos), newPos, level, random)
    }
}
