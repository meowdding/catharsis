package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

object GuiElements {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiElement>>()

    @IncludedCodec
    val CODEC: Codec<GuiElement> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiElement::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("player"), CatharsisCodecs.getMapCodec<GuiPlayerElement>())
        ID_MAPPER.put(Catharsis.id("sprite"), CatharsisCodecs.getMapCodec<GuiSpriteElement>())
        ID_MAPPER.put(Catharsis.id("text"), CatharsisCodecs.getMapCodec<GuiTextElement>())
    }
}

object GuiWidgetElements {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiWidgetElement>>()

    @IncludedCodec
    val CODEC: Codec<GuiWidgetElement> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiWidgetElement::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("button"), CatharsisCodecs.getMapCodec<GuiButtonElement>())
    }
}
