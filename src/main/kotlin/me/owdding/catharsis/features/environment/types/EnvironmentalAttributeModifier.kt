package me.owdding.catharsis.features.environment

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.environment.conditions.ConstantCondition
import me.owdding.catharsis.features.environment.conditions.EnvironmentalModifierCondition
import me.owdding.catharsis.features.environment.provider.EnvironmentalAttributeProvider
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.EnvironmentAttributeLayer
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

data class EnvironmentalAttributeModifier<Value : Any>(
    val attribute: EnvironmentAttribute<Value>,
    override val condition: EnvironmentalModifierCondition<Value>,
    val provider: EnvironmentalAttributeProvider<Value>,
) : EnvironmentAttributeLayer.Positional<Value>, EnvironmentalModifier<Value> {

    companion object {
        fun <Value : Any> createCodec(attribute: EnvironmentAttribute<Value>): MapCodec<EnvironmentalAttributeModifier<Value>> = RecordCodecBuilder.mapCodec {
            it.group(
                EnvironmentalModifierCondition.createCodec(attribute.type().valueCodec).codec().optionalFieldOf("condition", ConstantCondition.True.asTyped()).forGetter(EnvironmentalAttributeModifier<Value>::condition),
                EnvironmentalAttributeProvider.createCodec(attribute).fieldOf("provider").forGetter(EnvironmentalAttributeModifier<Value>::provider),
            ).apply(it) { condition, provider ->
                EnvironmentalAttributeModifier(attribute, condition, provider)
            }
        }
    }

    override fun applyPositional(baseValue: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value {
        return when {
            condition.applies(baseValue, pos) -> {
                provider.getValue(baseValue, pos, biomeInterpolator)
            }

            else -> baseValue
        }
    }

    fun codec(): MapCodec<EnvironmentalAttributeModifier<Value>> = createCodec(attribute)
    override val codec: MapCodec<out EnvironmentalModifier<out Any>> get() = TODO()

    override fun register(environmentalModifierCollector: EnvironmentalModifierCollector) {
        environmentalModifierCollector.register(this)
    }
}
