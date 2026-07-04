package me.owdding.catharsis.features.environment

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.environment.conditions.EnvironmentalAttributeCondition
import me.owdding.catharsis.features.environment.provider.EnvironmentalAttributeProvider
import me.owdding.catharsis.utils.extensions.unsafeCast
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.EnvironmentAttributeLayer
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

data class EnvironmentalAttributeModifier<Value : Any>(
    val attribute: EnvironmentAttribute<Value>,
    val condition: EnvironmentalAttributeCondition<Value>,
    val provider: EnvironmentalAttributeProvider<Value>,
) : EnvironmentAttributeLayer.Positional<Value>, EnvironmentalModifier {

    companion object {
        fun <Value : Any> createCodec(attribute: EnvironmentAttribute<Value>): MapCodec<EnvironmentalAttributeModifier<out Any>> = RecordCodecBuilder.mapCodec {
            it.group(
                EnvironmentalAttributeCondition.createCodec(attribute.type().valueCodec).fieldOf("condition").forGetter(EnvironmentalAttributeModifier<Value>::condition),
                EnvironmentalAttributeProvider.createCodec(attribute).fieldOf("provider").forGetter(EnvironmentalAttributeModifier<Value>::provider),
            ).apply(it) { condition, provider ->
                EnvironmentalAttributeModifier(attribute, condition, provider)
            }
        }.unsafeCast()
    }

    override fun applyPositional(baseValue: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value {
        return when {
            condition.applies(baseValue, pos) -> {
                provider.getValue(baseValue, pos, biomeInterpolator)
            }

            else -> baseValue
        }
    }

    fun codec(): MapCodec<EnvironmentalAttributeModifier<out Any>> = createCodec(attribute)
    override val codec: MapCodec<out EnvironmentalModifier> get() = TODO()
}
