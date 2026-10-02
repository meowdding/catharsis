package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.EntityType

@GenerateCodec
data class PassengerEntityCondition(
    val index: Int = 0,
    val entityType: EntityType<*>?,
    val condition: EntityCondition,
) : EntityCondition {
    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<PassengerEntityCondition>()
    override fun matches(entity: EntityCheck): Boolean {
        val entity = entity.entity
        if (entity.passengers.isEmpty()) return false
        val passenger = entity.passengers.getOrNull(index) ?: return false
        return condition.matches(EntityCheck(passenger)) && (entityType != null || passenger.type == entityType)
    }
}
