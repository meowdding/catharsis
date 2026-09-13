package me.owdding.catharsis.features.environment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.environment.conditions.ConstantCondition
import me.owdding.catharsis.features.environment.conditions.EnvironmentalModifierCondition
import me.owdding.catharsis.features.environment.provider.EnvironmentalAttributeProvider
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.attribute.AttributeTypes
import net.minecraft.world.attribute.EnvironmentAttribute
import org.joml.Vector3f
import org.joml.Vector3fc

data class BiomeEffectModifier<Value : Any>(
    val effect: BiomeEffect<Value>,
    val provider: EnvironmentalAttributeProvider<Value>,
    override val condition: EnvironmentalModifierCondition<Value>,
) : EnvironmentalModifier<Value> {
    companion object {
        val CODEC: MapCodec<BiomeEffectModifier<out Any>> = BiomeEffect.CODEC.dispatchMap(
            { it.effect },
            { effect -> createCodec(effect) },
        )

        private fun <Type : Any> createCodec(effect: BiomeEffect<Type>) = RecordCodecBuilder.mapCodec {
            it.group(
                EnvironmentalAttributeProvider.createCodec(effect.attribute).fieldOf("provider").forGetter(BiomeEffectModifier<Type>::provider),
                EnvironmentalModifierCondition.createCodec(effect.attribute.valueCodec()).codec().optionalFieldOf("condition", ConstantCondition.True.asTyped()).forGetter(BiomeEffectModifier<Type>::condition),
            ).apply(it) { provider, condition -> BiomeEffectModifier(effect, provider, condition) }
        }
    }

    override val codec: MapCodec<out EnvironmentalModifier<out Any>> = CODEC

    override fun register(environmentalModifierCollector: EnvironmentalModifierCollector) {
        environmentalModifierCollector.register(this)
    }
}

//~ if >= 26.3 'Int' -> 'Vector3fc' {
//~ if >= 26.3 '-1' -> 'Vector3f()'
val baseColor: EnvironmentAttribute<Vector3fc> = EnvironmentAttribute.builder(AttributeTypes.RGB_COLOR).defaultValue(Vector3f()).build()

data object WaterColor : BiomeEffect<Vector3fc>(baseColor)
data object FoliageColor : BiomeEffect<Vector3fc>(baseColor)
data object DryFoliageColor : BiomeEffect<Vector3fc>(baseColor)
data object GrassColor : BiomeEffect<Vector3fc>(baseColor)
//~}

sealed class BiomeEffect<Value : Any>(val attribute: EnvironmentAttribute<Value>) {
    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, BiomeEffect<out Any>>()

        val CODEC: MapCodec<BiomeEffect<out Any>> = ID_MAPPER.codec(Codec.STRING).fieldOf("effect")

        init {
            ID_MAPPER.put("water_color", WaterColor)
            ID_MAPPER.put("foliage_color", FoliageColor)
            ID_MAPPER.put("dry_foliage_color", DryFoliageColor)
            ID_MAPPER.put("grass_color", GrassColor)
        }
    }
}
