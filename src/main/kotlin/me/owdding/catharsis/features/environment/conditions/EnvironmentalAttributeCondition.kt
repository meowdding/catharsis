package me.owdding.catharsis.features.environment.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.utils.extensions.unsafeCast
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.attribute.AttributeType
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

interface EnvironmentalAttributeCondition<Value : Any> {
    val codec: MapCodec<out EnvironmentalAttributeCondition<Value>> get() = TODO()

    fun applies(baseValue: Value, pos: Vec3): Boolean

    companion object {
        fun <Value : Any> createCodec(typeCodec: Codec<Value>): MapCodec<EnvironmentalAttributeCondition<Value>> {
            val mapper = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out EnvironmentalAttributeCondition<Value>>>()

            mapper.put("true", MapCodec.unit<EnvironmentalAttributeCondition<Value>> { ConstantCondition.True.unsafeCast() })
            mapper.put("false", MapCodec.unit<EnvironmentalAttributeCondition<Value>> { ConstantCondition.False.unsafeCast() })

            return mapper.codec(Codec.STRING).dispatchMap(EnvironmentalAttributeCondition<Value>::codec) { it }
        }
    }
}
