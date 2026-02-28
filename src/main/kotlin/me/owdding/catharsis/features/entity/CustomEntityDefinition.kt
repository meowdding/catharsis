package me.owdding.catharsis.features.entity

import me.owdding.catharsis.features.entity.conditions.EntityCondition
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType


@GenerateCodec
data class CustomEntityDefinition(
    val target: EntityCondition,
    val type: EntityType<*>
) {
    fun matches(entity: Entity): Boolean {
        if (entity.type != type) return false

        return target.matches(entity)
    }
}
