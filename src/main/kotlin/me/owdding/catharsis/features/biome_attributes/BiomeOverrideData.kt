package me.owdding.catharsis.features.biome_attributes

import me.owdding.catharsis.features.biome_attributes.matcher.BiomeMatcher
import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class BiomeOverrideData(
    val matcher: BiomeMatcher
) {
}


