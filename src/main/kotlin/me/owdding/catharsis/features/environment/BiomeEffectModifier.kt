package me.owdding.catharsis.features.environment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.environment.conditions.EnvironmentalAttributeCondition
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.biome.BiomeSpecialEffects

data class BiomeEffectModifier<Value : Any>(
    val effect: BiomeEffect<Value>,
    val value: Value,
    val condition: EnvironmentalAttributeCondition<Value>,
) : EnvironmentalModifier {
    companion object {
        val CODEC: MapCodec<BiomeEffectModifier<out Any>> = BiomeEffect.CODEC.dispatchMap(
            { it.effect },
            { effect -> createCodec(effect) },
        )

        private fun <Type : Any> createCodec(effect: BiomeEffect<Type>) = RecordCodecBuilder.mapCodec {
            it.group(
                effect.valueCodec.fieldOf("value").forGetter(BiomeEffectModifier<Type>::value),
                EnvironmentalAttributeCondition.createCodec(effect.valueCodec).fieldOf("condition").forGetter(BiomeEffectModifier<Type>::condition),
            ).apply(it) { value, condition -> BiomeEffectModifier(effect, value, condition) }
        }
    }

    override val codec: MapCodec<out EnvironmentalModifier> = CODEC
}

data object WaterColor : BiomeEffect<Int>(ExtraCodecs.STRING_RGB_COLOR)
data object FoliageColor : BiomeEffect<Int>(ExtraCodecs.STRING_RGB_COLOR)
data object DryFoliageColor : BiomeEffect<Int>(ExtraCodecs.STRING_RGB_COLOR)
data object GrassColor : BiomeEffect<Int>(ExtraCodecs.STRING_RGB_COLOR)
data object GrassColorModifier : BiomeEffect<BiomeSpecialEffects.GrassColorModifier>(BiomeSpecialEffects.GrassColorModifier.CODEC)

sealed class BiomeEffect<Value : Any>(val valueCodec: Codec<Value>) {
    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, BiomeEffect<out Any>>()

        val CODEC: MapCodec<BiomeEffect<out Any>> = ID_MAPPER.codec(Codec.STRING).fieldOf("effect")

        init {
            ID_MAPPER.put("water_color", WaterColor)
            ID_MAPPER.put("foliage_color", FoliageColor)
            ID_MAPPER.put("dry_foliage_color", DryFoliageColor)
            ID_MAPPER.put("grass_color", GrassColor)
            ID_MAPPER.put("grass_color_modifier", GrassColorModifier)
        }
    }
}
