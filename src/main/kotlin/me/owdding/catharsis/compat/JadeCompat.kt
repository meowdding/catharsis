package me.owdding.catharsis.compat

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.blocks.BlockDisplayDefinition
import me.owdding.catharsis.features.blocks.BlockDisplayDefinition.Companion.isSkyblockBlock
import me.owdding.catharsis.features.blocks.BlockReplacements
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult
import snownee.jade.api.*
import snownee.jade.api.callback.JadeItemModNameCallback
import snownee.jade.api.callback.JadeRayTraceCallback
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.ui.Element
import snownee.jade.api.ui.JadeUI

object JadeCompat : IWailaPlugin, JadeRayTraceCallback, JadeItemModNameCallback {

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(CustomBlockProvider, Block::class.java)
        registration.registerBlockIcon(CustomBlockProvider, Block::class.java)
        registration.registerBlockComponent(RemoveVanillaInfoProvider, Block::class.java)
        registration.addRayTraceCallback(10, this)
        registration.addItemModNameCallback(this)
    }

    override fun onRayTrace(result: HitResult, accessor: Accessor<*>, original: Accessor<*>): Accessor<*> {
        val blockAccessor = accessor as? BlockAccessor ?: return original
        val display = BlockReplacements.getDisplay(blockAccessor.blockState, blockAccessor.position) ?: return original

        return WrappedBlockAccessor(display, blockAccessor)
    }

    override fun gatherItemModName(stack: ItemStack): String? {
        if (!stack.isSkyblockBlock()) return null
        return "SkyBlock"
    }

    private object RemoveVanillaInfoProvider : IBlockComponentProvider {
        override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
            if (accessor !is WrappedBlockAccessor) return

            tooltip.remove(JadeIds.MC_HARVEST_TOOL)
        }

        override fun getUid(): Identifier = Catharsis.id("remove_vanilla_info")
        override fun isRequired(): Boolean = true
        override fun enabledByDefault(): Boolean = true
        override fun getDefaultPriority(): Int = 100000
    }

    private object CustomBlockProvider : IBlockComponentProvider {

        override fun isRequired(): Boolean = true
        override fun getUid(): Identifier = Catharsis.id("custom_block_info")

        override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
            if (accessor !is WrappedBlockAccessor || accessor.display.lore == null) return

            tooltip.addAll(accessor.display.lore)
        }

        override fun getIcon(accessor: BlockAccessor, config: IPluginConfig, icon: Element?): Element? {
            if (accessor !is WrappedBlockAccessor || accessor.display.model == null) return icon

            return JadeUI.item(accessor.stack)
        }
    }

    private class WrappedBlockAccessor(
        val display: BlockDisplayDefinition,
        val parent: BlockAccessor
    ): BlockAccessor by parent {

        val stack = this.display.createItemStack(this.parent.block, this.parent.serversideRep)

        override fun <T : BlockEntity> typedBlockEntity(): T = parent.typedBlockEntity()
        override fun getAccessorType(): Class<out Accessor<*>> = parent.accessorType
        override fun isServersideContent(): Boolean = true
        override fun getServersideRep(): ItemStack = stack
    }
}
