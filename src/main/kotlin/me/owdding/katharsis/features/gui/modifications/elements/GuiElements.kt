package me.owdding.katharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs

object GuiElements {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiElement>>()

    @IncludedCodec
    val CODEC: Codec<GuiElement> = ID_MAPPER.codec(Identifier.CODEC).dispatch(GuiElement::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("player"), KatharsisCodecs.getMapCodec<GuiPlayerElement>())
        ID_MAPPER.put(Katharsis.id("entity"), KatharsisCodecs.getMapCodec<GuiEntityElement>())
        ID_MAPPER.put(Katharsis.id("sprite"), KatharsisCodecs.getMapCodec<GuiSpriteElement>())
        ID_MAPPER.put(Katharsis.id("text"), KatharsisCodecs.getMapCodec<GuiTextElement>())
    }
}

object GuiWidgetElements {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiWidgetElement>>()

    @IncludedCodec
    val CODEC: Codec<GuiWidgetElement> = ID_MAPPER.codec(Identifier.CODEC).dispatch(GuiWidgetElement::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("button"), KatharsisCodecs.getMapCodec<GuiButtonElement>())
        ID_MAPPER.put(Katharsis.id("item_stack"), KatharsisCodecs.getMapCodec<GuiItemStackElement>())
    }
}
