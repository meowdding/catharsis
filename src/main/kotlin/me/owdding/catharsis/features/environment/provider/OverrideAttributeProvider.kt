package me.owdding.catharsis.features.environment.provider

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.attribute.AttributeType
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

data class OverrideAttributeProvider<Value : Any>(
    val value: Value,
) : EnvironmentalAttributeProvider<Value> {
    companion object {
        fun <Value : Any> createCodec(type: AttributeType<Value>) = type.valueCodec.fieldOf("value").xmap(::OverrideAttributeProvider, OverrideAttributeProvider<Value>::value)
    }

    override fun getValue(base: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value = value
}
