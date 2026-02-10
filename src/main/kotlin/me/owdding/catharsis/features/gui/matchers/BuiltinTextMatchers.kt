package me.owdding.catharsis.features.gui.matchers

import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import org.intellij.lang.annotations.Language

@GenerateCodec
data class EqualsTextMatcher(
    @Compact @FieldNames("name", "text") val name: Set<String>
): TextMatcher {
    constructor(vararg name: String) : this(setOf(*name))

    override val codec = CatharsisCodecs.getMapCodec<EqualsTextMatcher>()
    override fun matches(text: String): Boolean = this.name.any { it == text }
}

@GenerateCodec
data class RegexTextMatcher(
    @FieldNames("name", "text") val name: Regex
): TextMatcher {
    constructor(@Language("RegExp") regex: String) : this(Regex(regex))

    override val codec = CatharsisCodecs.getMapCodec<RegexTextMatcher>()
    override fun matches(text: String): Boolean = this.name.matches(text)
}

