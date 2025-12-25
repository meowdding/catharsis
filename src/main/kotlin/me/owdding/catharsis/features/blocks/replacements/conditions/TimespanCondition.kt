package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.BlockReplacements
import me.owdding.catharsis.features.timespan.Timespans
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

@GenerateCodec
data class TimespanCondition(
    @FieldName("timespan") @NamedCodec("catharsis_identifier") val identifier: Identifier,
) : BlockCondition {

    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.TimespanConditionCodec

    val timespan by lazy {
        Timespans.getLoadedTimespans()[identifier]?.apply {
            markUsed()
        } ?: run {
            BlockReplacements.warn("Requested unknown timespan $identifier!")
            null
        }
    }

    override fun check(
        state: BlockState,
        pos: BlockPos,
        level: Level,
        random: RandomSource,
    ): Boolean = timespan?.test() == true
}
