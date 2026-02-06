package me.owdding.catharsis.features.gui.definitions.slots

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.ItemStack

interface SlotCondition {

    val codec: MapCodec<out SlotCondition>

    fun matches(slot: Int, stack: ItemStack): Boolean
}

object SlotConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out SlotCondition>>()

    @IncludedCodec
    val CODEC: Codec<SlotCondition> = ID_MAPPER.codec(IncludedCodecs.catharsisIdentifier).dispatch(SlotCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("any"), CatharsisCodecs.getMapCodec<SlotAnyCondition>())
        ID_MAPPER.put(Catharsis.id("all"), CatharsisCodecs.getMapCodec<SlotAllCondition>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<SlotIndexCondition>())
        ID_MAPPER.put(Catharsis.id("id"), CatharsisCodecs.getMapCodec<SlotSkyBlockIdCondition>())
        ID_MAPPER.put(Catharsis.id("item"), CatharsisCodecs.getMapCodec<SlotItemCondition>())
        ID_MAPPER.put(Catharsis.id("name"), CatharsisCodecs.getMapCodec<SlotNameCondition>())
        ID_MAPPER.put(Catharsis.id("has_component"), CatharsisCodecs.getMapCodec<HasComponentCondition>())
        ID_MAPPER.put(Catharsis.id("islands"), CatharsisCodecs.getMapCodec<SlotIslandCondition>())
        ID_MAPPER.put(Catharsis.id("texture"), CatharsisCodecs.getMapCodec<SlotTextureCondition>())
        ID_MAPPER.put(Catharsis.id("is_tooltip_hidden"), IsTooltipHiddenCondition.codec)
    }
}
