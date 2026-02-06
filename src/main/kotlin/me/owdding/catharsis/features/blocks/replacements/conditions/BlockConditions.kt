package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

interface BlockCondition {

    val codec: MapCodec<out BlockCondition>

    fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean
}

object BlockConditions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out BlockCondition>>()
    @IncludedCodec val CODEC: Codec<BlockCondition> = Codec.withAlternative(
        ID_MAPPER.codec(Codec.STRING).dispatchMap(BlockCondition::codec) { it }.codec(),
        IncludedCodecs.tagOrBlocksCodec.flatComapMap(::BlockIdCondition) { DataResult.error {
            "BlockConditions.CODEC.IncludedCodecs.tagOrBlocksCodec Should never encode!"
        }}
    )

    init {
        ID_MAPPER.put("always", AlwaysBlockCondition.codec)
        ID_MAPPER.put("never", NeverBlockCondition.codec)
        ID_MAPPER.put("or", CatharsisCodecs.OrBlockConditionCodec)
        ID_MAPPER.put("and", CatharsisCodecs.AndBlockConditionCodec)
        ID_MAPPER.put("not", CatharsisCodecs.NotBlockConditionCodec)
        ID_MAPPER.put("id", CatharsisCodecs.BlockIdConditionCodec)
        ID_MAPPER.put("properties", CatharsisCodecs.PropertiesConditionCodec)
        ID_MAPPER.put("relative", CatharsisCodecs.RelativeConditionCodec)
        ID_MAPPER.put("in_island", CatharsisCodecs.InIslandConditionCodec)
        ID_MAPPER.put("timespan", CatharsisCodecs.TimespanConditionCodec)
        ID_MAPPER.put("dungeon_floor", CatharsisCodecs.DungeonFloorConditionCodec)
    }
}

data object AlwaysBlockCondition : BlockCondition {
    override val codec: MapCodec<out BlockCondition> = MapCodec.unit(AlwaysBlockCondition)

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean = true
}

data object NeverBlockCondition : BlockCondition {
    override val codec: MapCodec<out BlockCondition> = MapCodec.unit(NeverBlockCondition)
    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource, ): Boolean = false
}
