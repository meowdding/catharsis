package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec

data object HasPassengerEntityCondition : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = MapCodec.unit { HasPassengerEntityCondition }
    override fun matches(entity: EntityCheck): Boolean = entity.entity.passengers.isNotEmpty()
}
