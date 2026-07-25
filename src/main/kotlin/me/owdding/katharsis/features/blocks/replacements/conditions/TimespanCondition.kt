package me.owdding.katharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.blocks.BlockReplacements
import me.owdding.katharsis.features.timespan.Timespans
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState

@GenerateCodec
data class TimespanCondition(
    @FieldName("timespan") @NamedCodec("katharsis_identifier") val identifier: Identifier,
) : BlockCondition {

    override val codec: MapCodec<out BlockCondition> = KatharsisCodecs.TimespanConditionCodec

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
        level: BlockAndTintGetter,
        random: RandomSource,
    ): Boolean = timespan?.test() == true
}
