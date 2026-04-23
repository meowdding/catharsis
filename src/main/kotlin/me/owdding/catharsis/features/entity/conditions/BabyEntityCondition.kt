package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.Zoglin
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.monster.zombie.Zombie

@GenerateCodec
data class BabyEntityCondition(val isBaby: Boolean) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<BabyEntityCondition>()

    override fun matches(entity: Entity) = when (entity) {
        is AgeableMob -> isBaby
        is Zombie -> isBaby
        is Piglin -> isBaby
        is Zoglin -> isBaby
        else -> false
    } == isBaby
}
