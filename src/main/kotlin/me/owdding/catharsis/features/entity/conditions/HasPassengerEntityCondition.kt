package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import net.minecraft.world.entity.Entity

data object HasPassengerEntityCondition : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = MapCodec.unit { HasPassengerEntityCondition }
    override fun matches(entity: Entity): Boolean = entity.passengers.isNotEmpty()
}
