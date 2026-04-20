package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import kotlin.jvm.optionals.getOrNull

@GenerateCodec
data class BiomeCondition(val biome: Identifier) : BlockCondition {
    override val codec: MapCodec<out BlockCondition> = CODEC

    override fun check(
        state: BlockState,
        pos: BlockPos,
        level: BlockAndTintGetter,
        random: RandomSource,
    ): Boolean = McLevel.selfOrNull?.getBiome(pos)?.unwrapKey()?.getOrNull() == biome

    companion object {
        val CODEC: MapCodec<out BlockCondition> = CatharsisCodecs.BiomeConditionCodec
    }
}
