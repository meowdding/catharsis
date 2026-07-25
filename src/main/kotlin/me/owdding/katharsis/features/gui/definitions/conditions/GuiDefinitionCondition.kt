package me.owdding.katharsis.features.gui.definitions.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.inventory.Slot

interface GuiDefinitionCondition {

    val codec: MapCodec<out GuiDefinitionCondition>
    val cost: Int get() = 0

    fun matches(slots: List<Slot>, screen: AbstractContainerScreen<*>): Boolean
    fun optimize(): GuiDefinitionCondition = this
}

object GuiDefConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiDefinitionCondition>>()

    @IncludedCodec
    val CODEC: Codec<GuiDefinitionCondition> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier)
        .dispatch(GuiDefinitionCondition::codec) { it }
        .xmap(GuiDefinitionCondition::optimize) { it }

    init {
        ID_MAPPER.put(Katharsis.id("any"), KatharsisCodecs.getMapCodec<GuiDefinitionAnyCondition>())
        ID_MAPPER.put(Katharsis.id("all"), KatharsisCodecs.getMapCodec<GuiDefinitionAllCondition>())
        ID_MAPPER.put(Katharsis.id("not"), KatharsisCodecs.getMapCodec<GuiDefinitionNotCondition>())
        ID_MAPPER.put(Katharsis.id("slot"), KatharsisCodecs.getMapCodec<GuiDefinitionSlotCondition>())
        ID_MAPPER.put(Katharsis.id("title"), KatharsisCodecs.getMapCodec<GuiDefinitionTitleCondition>())
        ID_MAPPER.put(Katharsis.id("type"), KatharsisCodecs.getMapCodec<GuiDefinitionTypeCondition>())
        ID_MAPPER.put(Katharsis.id("islands"), KatharsisCodecs.getMapCodec<GuiDefinitionIslandCondition>())
        ID_MAPPER.put(Katharsis.id("external_mod_config"), KatharsisCodecs.getMapCodec<GuiDefinitionExternalModConfigCondition>())
    }
}
