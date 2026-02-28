package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity

@GenerateCodec
data class AnyEntityCondition(
    val conditions: List<EntityCondition>
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<AnyEntityCondition>()
    override val cost: Int = this.conditions.sumOf { it.cost } + 1

    override fun matches(entity: Entity): Boolean = conditions.any { it.matches(entity) }
    override fun optimize(): EntityCondition = AnyEntityCondition(this.conditions.map(EntityCondition::optimize).sortedBy(EntityCondition::cost))
}
