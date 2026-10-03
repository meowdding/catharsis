package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class NotEntityCondition(val condition: EntityCondition) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<NotEntityCondition>()
    override val cost: Int = this.condition.cost + 1

    override fun matches(entity: EntityCheck): Boolean = !condition.matches(entity)
}
