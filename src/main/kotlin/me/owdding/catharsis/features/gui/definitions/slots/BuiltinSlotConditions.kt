package me.owdding.catharsis.features.gui.definitions.slots

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.types.IntPredicate
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@GenerateCodec
data class SlotAllCondition(
    val conditions: List<SlotCondition>
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotAllCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = this.conditions.all { it.matches(slot, stack) }
}

@GenerateCodec
data class SlotAnyCondition(
    val conditions: List<SlotCondition>
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotAnyCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = this.conditions.any { it.matches(slot, stack) }
}

@GenerateCodec
data class SlotIndexCondition(
    val slot: IntPredicate
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotIndexCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = slot == -1 || slot in this.slot
}

@GenerateCodec
data class SlotSkyBlockIdCondition(
    val ids: Set<String>
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotSkyBlockIdCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = this.ids.contains(stack.getData(DataTypes.API_ID))
}

@GenerateCodec
data class SlotItemCondition(
    val items: Set<Item>
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotItemCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = stack.item in this.items
}

@GenerateCodec
data class SlotNameCondition(
    val name: Regex
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotNameCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = this.name.matches(stack.hoverName.stripped)
}

@GenerateCodec
data class HasComponentCondition(
    val component: DataComponentType<*>
): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<HasComponentCondition>()
    override fun matches(slot: Int, stack: ItemStack): Boolean = stack.has(component)
}

data object IsTooltipHiddenCondition: SlotCondition {
    override val codec: MapCodec<IsTooltipHiddenCondition> = MapCodec.unit { this }
    override fun matches(slot: Int, stack: ItemStack): Boolean = stack.get(DataComponents.TOOLTIP_DISPLAY)?.hideTooltip == true
}
