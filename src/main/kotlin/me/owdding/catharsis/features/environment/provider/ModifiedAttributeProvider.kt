package me.owdding.catharsis.features.environment.provider

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Util
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.attribute.modifier.AttributeModifier
import net.minecraft.world.phys.Vec3

data class ModifiedAttributeProvider<Value : Any, Argument : Any>(
    val modifier: AttributeModifier<Value, Argument>,
    val value: Argument,
) : EnvironmentalAttributeProvider<Value> {
    override fun getValue(base: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value = modifier.apply(base, value)

    companion object {
        fun <Value : Any, Argument : Any> createFullCodec(attribute: EnvironmentAttribute<Value>, modifier: AttributeModifier<Value, Argument>): MapCodec<ModifiedAttributeProvider<Value, Argument>> {
            return RecordCodecBuilder.mapCodec {
                it.group(
                    modifier.argumentCodec(attribute).fieldOf("argument").forGetter(ModifiedAttributeProvider<Value, Argument>::value)
                ).apply(it) {
                    ModifiedAttributeProvider(modifier, it)
                }
            }
        }

        fun <Value : Any> createCodec(attribute: EnvironmentAttribute<Value>): MapCodec<out EnvironmentalAttributeProvider<Value>> = Util.memoize { attribute: EnvironmentAttribute<Value> ->
            attribute.type().modifierCodec().dispatchMap("modifier", { it.modifier }) { createFullCodec(attribute, it) }
        }.apply(attribute)
    }

}
