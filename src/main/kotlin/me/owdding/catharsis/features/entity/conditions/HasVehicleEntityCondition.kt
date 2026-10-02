package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec

data object HasVehicleEntityCondition : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = MapCodec.unit { HasVehicleEntityCondition }
    override fun matches(entity: EntityCheck): Boolean = entity.entity.vehicle != null
}
