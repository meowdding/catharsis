package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

@GenerateCodec
data class VehicleEntityCondition(
    val entityType: EntityType<*>?,
    val condition: EntityCondition,
) : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<VehicleEntityCondition>()
    override fun matches(entity: Entity): Boolean {
        val vehicle = entity.vehicle ?: return false
        return condition.matches(vehicle) && (entityType != null || vehicle.type == entityType)
    }
}
