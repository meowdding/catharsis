package me.owdding.catharsis.features.environment.conditions

import com.mojang.serialization.MapCodec
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

sealed class ConstantCondition<Value : Any>(val value: Boolean) : EnvironmentalAttributeCondition<Value> {
    override fun applies(baseValue: Value, pos: Vec3): Boolean = value


    data object True : ConstantCondition<Any>(true)
    data object False : ConstantCondition<Any>(true)
}
