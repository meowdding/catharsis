package me.owdding.catharsis.features.environment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.environment.conditions.ConstantCondition
import me.owdding.catharsis.features.environment.conditions.EnvironmentalModifierCondition
import me.owdding.catharsis.features.environment.provider.EnvironmentalAttributeProvider
import net.minecraft.util.ARGB
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.attribute.AttributeTypes
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import org.joml.Vector3fc

data class BiomeEffectModifier<Value : Any, DataValue : Any>(
    val effect: BiomeEffect<Value, DataValue>,
    val provider: EnvironmentalAttributeProvider<Value>,
    override val condition: EnvironmentalModifierCondition<Value>,
) : EnvironmentalModifier<Value>{
    companion object {
        val CODEC: MapCodec<BiomeEffectModifier<out Any, out Any>> = BiomeEffect.CODEC.dispatchMap(
            { it.effect },
            { effect -> createCodec(effect) },
        )

        private fun <Type : Any, DataValue : Any> createCodec(effect: BiomeEffect<Type, DataValue>) = RecordCodecBuilder.mapCodec {
            it.group(
                EnvironmentalAttributeProvider.createCodec(effect.attribute).fieldOf("provider").forGetter(BiomeEffectModifier<Type, DataValue>::provider),
                EnvironmentalModifierCondition.createCodec(effect.attribute.valueCodec()).codec().optionalFieldOf("condition", ConstantCondition.True.asTyped()).forGetter(BiomeEffectModifier<Type, DataValue>::condition),
            ).apply(it) { provider, condition -> BiomeEffectModifier(effect, provider, condition) }
        }
    }

    override val codec: MapCodec<out EnvironmentalModifier<out Any>> = CODEC
    fun getBaseValue(base: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value? {
        return this.provider.getValue(base, pos, biomeInterpolator)
    }
    fun getValue(base: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): DataValue? {
        return getBaseValue(base, pos, biomeInterpolator)?.let(this.effect.to)
    }
    fun getDataValue(base: DataValue, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): DataValue? {
        return getValue(this.effect.from(base), pos, biomeInterpolator)
    }

    override fun register(environmentalModifierCollector: EnvironmentalModifierCollector) {
        environmentalModifierCollector.register(this)
    }
}

//~ if >= 26.3 '<Int' -> '<Vector3fc', '{ vec -> vec }' -> 'ARGB::colorFromVector3f', '{ color -> color }' -> 'ARGB::vector3fFromRGB24' {
//~ if >= 26.3 '-1' -> 'Vector3f(1f)'
val baseColor: EnvironmentAttribute<Vector3fc> = EnvironmentAttribute.builder(AttributeTypes.RGB_COLOR).defaultValue(Vector3f(1f)).build()

data object WaterColor : BiomeEffect<Vector3fc, Int>(baseColor, ARGB::colorFromVector3f, ARGB::vector3fFromRGB24)
data object FoliageColor : BiomeEffect<Vector3fc, Int>(baseColor, ARGB::colorFromVector3f, ARGB::vector3fFromRGB24)
data object DryFoliageColor : BiomeEffect<Vector3fc, Int>(baseColor, ARGB::colorFromVector3f, ARGB::vector3fFromRGB24)
data object GrassColor : BiomeEffect<Vector3fc, Int>(baseColor, ARGB::colorFromVector3f, ARGB::vector3fFromRGB24)
//~}

sealed class BiomeEffect<Value : Any, DataValue : Any>(val attribute: EnvironmentAttribute<Value>, val to: (Value) -> DataValue, val from: (DataValue) -> Value) {
    companion object {
        val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<String, BiomeEffect<out Any, out Any>>()

        val CODEC: MapCodec<BiomeEffect<out Any, out Any>> = ID_MAPPER.codec(Codec.STRING).fieldOf("effect")

        init {
            ID_MAPPER.put("water_color", WaterColor)
            ID_MAPPER.put("foliage_color", FoliageColor)
            ID_MAPPER.put("dry_foliage_color", DryFoliageColor)
            ID_MAPPER.put("grass_color", GrassColor)
        }
    }
}
