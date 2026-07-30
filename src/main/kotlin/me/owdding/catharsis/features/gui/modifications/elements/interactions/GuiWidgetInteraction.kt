package me.owdding.catharsis.features.gui.modifications.elements.interactions

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.generated.CatharsisCodecs.getCodec
import me.owdding.catharsis.generated.CodecUtils
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.OptionalNullable
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import java.util.*

data class GuiWidgetClickInteractions(
    val left: GuiWidgetInteraction? = null,
    val right: GuiWidgetInteraction? = null,
    val middle: GuiWidgetInteraction? = null,
) {
    fun getForButton(button: Int): GuiWidgetInteraction? = when (button) {
        0 -> left
        1 -> right
        2 -> middle
        else -> null
    }

    companion object {
        val NO_OP = GuiWidgetClickInteractions(left = GuiNoOpWidgetInteraction)


        private val GuiWidgetClickInteractionsCodec: MapCodec<GuiWidgetClickInteractions> = CodecUtils.lazyMapCodec {
            RecordCodecBuilder.mapCodec {
                it.group(
                    getCodec<GuiWidgetInteraction>().optionalFieldOf("left").forGetter { getter -> Optional.ofNullable(getter.left) },
                    getCodec<GuiWidgetInteraction>().optionalFieldOf("right").forGetter { getter -> Optional.ofNullable(getter.right) },
                    getCodec<GuiWidgetInteraction>().optionalFieldOf("middle").forGetter { getter -> Optional.ofNullable(getter.middle) },
                ).apply(it) { left, right, middle ->
                    if (listOf(left, right, middle).all(Optional<*>::isEmpty)) {
                        throw IllegalStateException("At least one click action must be specified!")
                    }

                    GuiWidgetClickInteractions(left = left.orElse(null), right = right.orElse(null), middle = middle.orElse(null))
                }
            }
        }

        @IncludedCodec
        val CODEC: Codec<GuiWidgetClickInteractions> = Codec.either(
            GuiWidgetInteractions.CODEC,
            GuiWidgetClickInteractionsCodec.codec(),
        ).xmap(
            { either -> either.map({ legacyInteraction -> GuiWidgetClickInteractions(left = legacyInteraction, right = legacyInteraction) }, { it }) },
            Either<*, GuiWidgetClickInteractions>::right,
        )
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
