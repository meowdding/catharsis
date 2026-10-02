package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import net.minecraft.world.entity.Entity

data object HasVehicleEntityCondition : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = MapCodec.unit { HasVehicleEntityCondition }
    override fun matches(entity: Entity): Boolean = entity.vehicle != null
}
