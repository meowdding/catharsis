package me.owdding.catharsis.features.text.replacers

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs

interface TextReplacer {

    val codec: MapCodec<out TextReplacer>

    fun replace(text: Component): ReplacementResult
}

sealed class ReplacementResult(val text: Component, val replaced: Boolean = true) {
    class Continue(text: Component, replaced: Boolean) : ReplacementResult(text, replaced)
    class Break(text: Component, replaced: Boolean) : ReplacementResult(text, replaced)
}

object TextReplacers {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out TextReplacer>>()

    @IncludedCodec
    val CODEC: Codec<TextReplacer> = ID_MAPPER.codec(Identifier.CODEC).dispatch(TextReplacer::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("regex"), CatharsisCodecs.getMapCodec<RegexTextReplacer>())
        ID_MAPPER.put(Catharsis.id("composite"), CatharsisCodecs.getMapCodec<CompositeTextReplacer>())
    }
}
