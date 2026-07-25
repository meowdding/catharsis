package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity

@GenerateCodec
data class AllEntityCondition(
    val conditions: List<EntityCondition>
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = KatharsisCodecs.getMapCodec<AllEntityCondition>()
    override val cost: Int = this.conditions.sumOf { it.cost } + 1

    override fun matches(entity: Entity): Boolean = conditions.all { it.matches(entity) }
    override fun optimize(): EntityCondition = AllEntityCondition(this.conditions.map(EntityCondition::optimize).sortedBy(EntityCondition::cost))
}
