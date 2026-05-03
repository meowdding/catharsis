package me.owdding.catharsis.features.blocks

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.helpers.McLevel


@GenerateCodec
data class BlockDisplayDefinition(
    val model: Identifier?,
    val name: Component?,
    val lore: List<Component>?,
) {

    fun createItemStack(block: Block, parent: ItemStack): ItemStack {
        val stack = if (parent.isEmpty) ItemStack(block) else parent.copy()
        stack[DataComponents.CUSTOM_DATA] = SKYBLOCK_BLOCK_CUSTOM_DATA

        if (model != null) stack[DataComponents.ITEM_MODEL] = model
        if (name != null) stack[DataComponents.ITEM_NAME] = name
        return stack
    }

    companion object {

        private val SKYBLOCK_BLOCK_CUSTOM_DATA = CustomData.of(CompoundTag())

        fun ItemStack.isSkyblockBlock(): Boolean {
            return this[DataComponents.CUSTOM_DATA] == SKYBLOCK_BLOCK_CUSTOM_DATA
        }
    }
}

data class BakedDisplayDefinition(
    val entries: BlockReplacementSelector<BlockDisplayDefinition>,
    val overrides: Map<Block, BlockReplacementSelector<BlockDisplayDefinition>>
) {

    fun select(state: BlockState, pos: BlockPos): BlockDisplayDefinition? {
        val random = RandomSource.create(Mth.getSeed(pos))

        val cacheState = BlockReplacements.blocksCache.getIfPresent(pos)
        val override = overrides[cacheState?.block]
        return entries.select(McLevel.selfOrNull, state, pos, random) ?: cacheState?.let { override?.select(McLevel.selfOrNull, cacheState, pos, random) }
    }
}
