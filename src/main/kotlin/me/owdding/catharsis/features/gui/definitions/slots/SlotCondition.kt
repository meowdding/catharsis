package me.owdding.catharsis.features.gui.definitions.slots

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.inventory.Slot

interface SlotCondition {

    val codec: MapCodec<out SlotCondition>

    fun matches(slot: Slot): Boolean
}

object SlotConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out SlotCondition>>()

    @IncludedCodec
    val CODEC: Codec<SlotCondition> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(SlotCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("any"), CatharsisCodecs.getMapCodec<SlotAnyCondition>())
        ID_MAPPER.put(Catharsis.id("all"), CatharsisCodecs.getMapCodec<SlotAllCondition>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<SlotIndexCondition>())
        ID_MAPPER.put(Catharsis.id("id"), CatharsisCodecs.getMapCodec<SlotSkyBlockIdCondition>())
        ID_MAPPER.put(Catharsis.id("item"), CatharsisCodecs.getMapCodec<SlotItemCondition>())
        ID_MAPPER.put(Catharsis.id("name"), CatharsisCodecs.getMapCodec<SlotNameCondition>())
    }
}
