package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

@GenerateCodec
data class BabyEntityCondition(val isBaby: Boolean) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<BabyEntityCondition>()

    override fun matches(entity: Entity) = (entity as? LivingEntity)?.isBaby == isBaby
}
