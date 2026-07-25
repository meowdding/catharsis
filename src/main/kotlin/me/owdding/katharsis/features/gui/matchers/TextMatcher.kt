package me.owdding.katharsis.features.gui.matchers

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.optionalDispatch
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import net.minecraft.util.ExtraCodecs

interface TextMatcher {

    val codec: MapCodec<out TextMatcher>
    val cost: Int get() = 0

    fun matches(text: String): Boolean
}

@Module
object TextMatchers {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out TextMatcher>>()

    @IncludedCodec(named = "text_matcher")
    val MAP_CODEC: MapCodec<TextMatcher> = ID_MAPPER.codec(Codec.STRING).optionalDispatch(
        "mode",
        TextMatcher::codec,
        KatharsisCodecs.getMapCodec<RegexTextMatcher>(),
    ) { it }

    @IncludedCodec
    val CODEC: Codec<TextMatcher> = Codec.either(
        Codec.STRING.xmap(::RegexTextMatcher) { it.name.toString() },
        MAP_CODEC.codec()
    ).xmap(Either<RegexTextMatcher, TextMatcher>::unwrap) { if (it is RegexTextMatcher) Either.left(it) else Either.right(it) }

    init {
        ID_MAPPER.put("equals", KatharsisCodecs.getMapCodec<EqualsTextMatcher>())
        ID_MAPPER.put("regex", KatharsisCodecs.getMapCodec<RegexTextMatcher>())
    }
}
