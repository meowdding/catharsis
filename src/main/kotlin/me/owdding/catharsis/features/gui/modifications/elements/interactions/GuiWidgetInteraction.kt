package me.owdding.catharsis.features.gui.modifications.elements.interactions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

interface GuiWidgetInteraction {

    val codec: MapCodec<out GuiWidgetInteraction>

    fun click(event: MouseButtonEvent)
}


object GuiWidgetInteractions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiWidgetInteraction>>()

    @IncludedCodec
    val CODEC: Codec<GuiWidgetInteraction> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiWidgetInteraction::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("link"), CatharsisCodecs.getMapCodec<GuiLinkWidgetInteraction>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<GuiSlotClickWidgetInteraction>())
        ID_MAPPER.put(Catharsis.id("command"), CatharsisCodecs.getMapCodec<GuiCommandWidgetInteraction>())
    }

}
