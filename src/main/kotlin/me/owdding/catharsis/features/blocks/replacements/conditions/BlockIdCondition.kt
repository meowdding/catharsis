package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.datafixers.util.Either
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

@GenerateCodec
data class BlockIdCondition(
    @NamedCodec("block_tag_or_list") val block: Either<TagKey<Block>, Set<Block>>
) : BlockCondition {

    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<BlockIdCondition>()
    override fun check(state: BlockState, pos: BlockPos, level: Level, random: RandomSource): Boolean {
        return block.map({ tag -> state.`is`(tag) }, { blocks -> blocks.contains(state.block) })
    }
}
