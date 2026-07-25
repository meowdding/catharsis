package me.owdding.katharsis.features.gui.modifications.elements.interactions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs

interface GuiWidgetInteraction {

    val codec: MapCodec<out GuiWidgetInteraction>

    fun click(button: Int)
}


object GuiWidgetInteractions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiWidgetInteraction>>()

    @IncludedCodec
    val CODEC: Codec<GuiWidgetInteraction> = ID_MAPPER.codec(Identifier.CODEC).dispatch(GuiWidgetInteraction::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("link"), KatharsisCodecs.getMapCodec<GuiLinkWidgetInteraction>())
        ID_MAPPER.put(Katharsis.id("slot"), KatharsisCodecs.getMapCodec<GuiSlotClickWidgetInteraction>())
        ID_MAPPER.put(Katharsis.id("slot_id"), KatharsisCodecs.getMapCodec<GuiSlotIdClickWidgetInteraction>())
        ID_MAPPER.put(Katharsis.id("command"), KatharsisCodecs.getMapCodec<GuiCommandWidgetInteraction>())
    }

}
