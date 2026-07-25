package me.owdding.katharsis.features.text

import me.owdding.katharsis.features.text.replacers.TextReplacer
import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class TextReplacement(
    val priority: Int = 0,
    val replacer: TextReplacer,
)
