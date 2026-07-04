package me.owdding.catharsis.features.environment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.util.ExtraCodecs

interface EnvironmentalModifier {

    val codec: MapCodec<out EnvironmentalModifier>

    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalModifier>>()

        val CODEC: Codec<EnvironmentalModifier> = ID_MAPPER.codec(Codec.STRING).dispatch(EnvironmentalModifier::codec) { it }

        init {
            ID_MAPPER.put("environmental_attribute", EnvironmentalModifiers.createEnvironmentalAttributeModifierCodec())
            ID_MAPPER.put("biome_effect", BiomeEffectModifier.CODEC)
        }
    }
}
