package me.owdding.catharsis.features.gui.matchers

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.optionalDispatch
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import net.minecraft.util.ExtraCodecs

interface TextMatcher {

    val codec: MapCodec<out TextMatcher>

    fun matches(text: String): Boolean
}

@Module
object TextMatchers {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out TextMatcher>>()

    @IncludedCodec
    val CODEC: MapCodec<TextMatcher> = ID_MAPPER.codec(Codec.STRING).optionalDispatch(
        "mode",
        TextMatcher::codec,
        CatharsisCodecs.getMapCodec<RegexTextMatcher>()
    ) { it }

    init {
        ID_MAPPER.put("equals", CatharsisCodecs.getMapCodec<EqualsTextMatcher>())
        ID_MAPPER.put("regex", CatharsisCodecs.getMapCodec<RegexTextMatcher>())
    }
}
