package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

@GenerateCodec
data class PassengerEntityCondition(
    val index: Int = 0,
    val entityType: EntityType<*>?,
    val condition: EntityCondition,
) : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = KatharsisCodecs.getMapCodec<PassengerEntityCondition>()
    override fun matches(entity: Entity): Boolean {
        if (entity.passengers.isEmpty()) return false
        val passenger = entity.passengers.getOrNull(index) ?: return false
        return condition.matches(passenger) && (entityType != null || passenger.type == entityType)
    }
}
