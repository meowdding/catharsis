package me.owdding.catharsis.features.environment.provider

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.attribute.EnvironmentAttribute
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

interface EnvironmentalAttributeProvider<Value> {

    val codec: MapCodec<out EnvironmentalAttributeProvider<Value>> get() = TODO()
    fun getValue(base: Value, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Value

    companion object {
        fun <Value : Any> createCodec(type: EnvironmentAttribute<Value>): MapCodec<EnvironmentalAttributeProvider<Value>> {
            val mapper = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalAttributeProvider<Value>>>()

            mapper.put("override", OverrideAttributeProvider.createCodec(type.type()))
            mapper.put("modified", ModifiedAttributeProvider.createCodec(type))

            return mapper.codec(Codec.STRING).dispatchMap(EnvironmentalAttributeProvider<Value>::codec) { it }
        }
    }
}
