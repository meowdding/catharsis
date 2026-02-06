package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.BlockReplacements
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor

@GenerateCodec
data class DungeonFloorCondition(
    val floor: DungeonFloor
) : BlockCondition {
    init {
        BlockReplacements.addDungeonFloor(floor)
    }

    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.DungeonFloorConditionCodec

    override fun check(
        state: BlockState,
        pos: BlockPos,
        level: BlockAndTintGetter,
        random: RandomSource,
    ): Boolean = DungeonAPI.dungeonFloor == floor
}
