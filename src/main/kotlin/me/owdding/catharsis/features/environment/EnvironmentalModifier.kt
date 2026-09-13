package me.owdding.catharsis.features.environment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.environment.conditions.EnvironmentalModifierCondition
import me.owdding.catharsis.features.environment.types.CompositeAttributeModifier
import me.owdding.catharsis.generated.CatharsisCodecs
import net.minecraft.util.ExtraCodecs

interface EnvironmentalModifier<Value : Any> {

    val codec: MapCodec<out EnvironmentalModifier<out Any>>

    val condition: EnvironmentalModifierCondition<Value>

    fun register(environmentalModifierCollector: EnvironmentalModifierCollector)

    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalModifier<out Any>>>()

        val CODEC: Codec<EnvironmentalModifier<out Any>> = ID_MAPPER.codec(Codec.STRING).dispatch(EnvironmentalModifier<out Any>::codec) { it }

        init {
            ID_MAPPER.put("environmental_attribute", EnvironmentalModifiers.createEnvironmentalAttributeModifierCodec())
            ID_MAPPER.put("biome_effect", BiomeEffectModifier.CODEC)
            ID_MAPPER.put("composite", CompositeAttributeModifier.CODEC)
        }
    }
}
