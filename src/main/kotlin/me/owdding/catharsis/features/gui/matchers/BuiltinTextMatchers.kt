package me.owdding.catharsis.features.gui.matchers

import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class EqualsTextMatcher(
    @Compact val name: Set<String>
): TextMatcher {
    override val codec = CatharsisCodecs.getMapCodec<EqualsTextMatcher>()
    override fun matches(text: String): Boolean = this.name.any { it == text }
}

@GenerateCodec
data class RegexTextMatcher(
   val name: Regex
): TextMatcher {
    override val codec = CatharsisCodecs.getMapCodec<EqualsTextMatcher>()
    override fun matches(text: String): Boolean = this.name.matches(text)
}

