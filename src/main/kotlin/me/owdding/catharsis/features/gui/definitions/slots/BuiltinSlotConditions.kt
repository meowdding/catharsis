package me.owdding.catharsis.features.gui.definitions.slots

import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.types.IntPredicate
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@GenerateCodec
data class SlotAllCondition(val conditions: List<SlotCondition>): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotAllCondition>()
    override fun matches(slot: Slot): Boolean = this.conditions.all { it.matches(slot) }
}

@GenerateCodec
data class SlotAnyCondition(val conditions: List<SlotCondition>): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotAnyCondition>()
    override fun matches(slot: Slot): Boolean = this.conditions.any { it.matches(slot) }
}

@GenerateCodec
data class SlotIndexCondition(val slot: IntPredicate): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotIndexCondition>()
    override fun matches(slot: Slot): Boolean = slot.index in this.slot
}

@GenerateCodec
data class SlotSkyBlockIdCondition(val ids: Set<String>): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotSkyBlockIdCondition>()
    override fun matches(slot: Slot): Boolean {
        val skyblockId = slot.item.getData(DataTypes.API_ID) ?: return false
        return skyblockId in this.ids
    }
}

@GenerateCodec
data class SlotItemCondition(val items: Set<Item>): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotItemCondition>()
    override fun matches(slot: Slot): Boolean {
        return slot.item.item in this.items
    }
}

@GenerateCodec
data class SlotNameCondition(val name: Regex): SlotCondition {
    override val codec = CatharsisCodecs.getMapCodec<SlotNameCondition>()
    override fun matches(slot: Slot): Boolean {
        return this.name.matches(slot.item.hoverName.stripped)
    }
}
