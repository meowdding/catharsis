package me.owdding.katharsis.features.gui.definitions.slots

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

interface SlotCondition {

    val codec: MapCodec<out SlotCondition>
    val cost: Int get() = 0

    fun matches(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean
    fun optimize(): SlotCondition = this

    operator fun invoke(slots: List<Slot>, slot: Slot): Boolean = matches(slots, slot.index, slot.item)
    operator fun invoke(slots: List<Slot>, slot: Int, stack: ItemStack): Boolean = matches(slots, slot, stack)
}

object SlotConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out SlotCondition>>()

    @IncludedCodec
    val CODEC: Codec<SlotCondition> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier)
        .dispatch(SlotCondition::codec) { it }
        .xmap(SlotCondition::optimize) { it }

    init {
        ID_MAPPER.put(Katharsis.id("any"), KatharsisCodecs.getMapCodec<SlotAnyCondition>())
        ID_MAPPER.put(Katharsis.id("all"), KatharsisCodecs.getMapCodec<SlotAllCondition>())
        ID_MAPPER.put(Katharsis.id("slot"), KatharsisCodecs.getMapCodec<SlotIndexCondition>())
        ID_MAPPER.put(Katharsis.id("id"), KatharsisCodecs.getMapCodec<SlotSkyBlockIdCondition>())
        ID_MAPPER.put(Katharsis.id("item"), KatharsisCodecs.getMapCodec<SlotItemCondition>())
        ID_MAPPER.put(Katharsis.id("item_model"), KatharsisCodecs.getMapCodec<SlotItemModelCondition>())
        ID_MAPPER.put(Katharsis.id("name"), KatharsisCodecs.getMapCodec<SlotNameCondition>())
        ID_MAPPER.put(Katharsis.id("lore"), KatharsisCodecs.getMapCodec<SlotLoreCondition>())
        ID_MAPPER.put(Katharsis.id("has_component"), KatharsisCodecs.getMapCodec<HasComponentCondition>())
        ID_MAPPER.put(Katharsis.id("islands"), KatharsisCodecs.getMapCodec<SlotIslandCondition>())
        ID_MAPPER.put(Katharsis.id("texture"), SlotTextureCondition.CODEC)
        ID_MAPPER.put(Katharsis.id("is_tooltip_hidden"), IsTooltipHiddenCondition.codec)
        ID_MAPPER.put(Katharsis.id("not"), KatharsisCodecs.getMapCodec<SlotNotCondition>())
        ID_MAPPER.put(Katharsis.id("relative_slot"), KatharsisCodecs.getMapCodec<RelativeSlotCondition>())
        ID_MAPPER.put(Katharsis.id("menu_border"), KatharsisCodecs.getMapCodec<SlotBorderCondition>())
    }
}
