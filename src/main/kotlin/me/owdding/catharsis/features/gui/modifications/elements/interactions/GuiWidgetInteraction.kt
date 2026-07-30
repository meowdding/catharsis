package me.owdding.catharsis.features.gui.modifications.elements.interactions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.OptionalNullable
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs

@GenerateCodec
data class GuiWidgetClickInteractions(
    @OptionalNullable val left: GuiWidgetInteraction? = null,
    @OptionalNullable val right: GuiWidgetInteraction? = null,
    @OptionalNullable val middle: GuiWidgetInteraction? = null,
) {
    fun getForButton(button: Int): GuiWidgetInteraction? = when (button) {
        0 -> left
        1 -> right
        2 -> middle
        else -> null
    }

    companion object {
        val NO_OP = GuiWidgetClickInteractions(left = GuiNoOpWidgetInteraction)

        @IncludedCodec
        val CODEC: Codec<GuiWidgetClickInteractions> = Codec.withAlternative(
            CatharsisCodecs.getCodec<GuiWidgetClickInteractions>(),
            GuiWidgetInteractions.CODEC,
        ) { legacyInteraction -> GuiWidgetClickInteractions(left = legacyInteraction, right = legacyInteraction) }
    }
}

interface GuiWidgetInteraction {

    val codec: MapCodec<out GuiWidgetInteraction>

    fun click(button: Int)
}


object GuiWidgetInteractions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiWidgetInteraction>>()

    @IncludedCodec
    val CODEC: Codec<GuiWidgetInteraction> = ID_MAPPER.codec(Identifier.CODEC).dispatch(GuiWidgetInteraction::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("link"), CatharsisCodecs.getMapCodec<GuiLinkWidgetInteraction>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<GuiSlotClickWidgetInteraction>())
        ID_MAPPER.put(Catharsis.id("slot_id"), CatharsisCodecs.getMapCodec<GuiSlotIdClickWidgetInteraction>())
        ID_MAPPER.put(Catharsis.id("command"), CatharsisCodecs.getMapCodec<GuiCommandWidgetInteraction>())
    }

}
