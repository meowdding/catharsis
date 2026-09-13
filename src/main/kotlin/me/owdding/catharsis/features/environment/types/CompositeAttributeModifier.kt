package me.owdding.catharsis.features.environment.types

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.environment.EnvironmentalModifier
import me.owdding.catharsis.features.environment.EnvironmentalModifierCollector
import me.owdding.catharsis.features.environment.conditions.TypelessEnvironmentalModifierCondition
import me.owdding.catharsis.generated.CodecUtils
import me.owdding.ktcodecs.IncludedCodec

data class CompositeAttributeModifier(
    override val condition: TypelessEnvironmentalModifierCondition,
    val modifiers: List<EnvironmentalModifier<out Any>>,
) : EnvironmentalModifier<Any> {
    companion object {
        @IncludedCodec
        val CODEC: MapCodec<CompositeAttributeModifier> = CodecUtils.lazyMapCodec {
            RecordCodecBuilder.mapCodec {
                it.group(
                    TypelessEnvironmentalModifierCondition.CODEC.fieldOf("condition").forGetter(CompositeAttributeModifier::condition),
                    EnvironmentalModifier.CODEC.listOf().fieldOf("modifiers").forGetter(CompositeAttributeModifier::modifiers),
                ).apply(it, ::CompositeAttributeModifier)
            }
        }
    }

    override val codec: MapCodec<out EnvironmentalModifier<out Any>> get() = CODEC

    override fun register(environmentalModifierCollector: EnvironmentalModifierCollector) {
        environmentalModifierCollector.pushCondition(this.condition).also { collector ->
            modifiers.forEach { it.register(collector) }
        }
    }
}
