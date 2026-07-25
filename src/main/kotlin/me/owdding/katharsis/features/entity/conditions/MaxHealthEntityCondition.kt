package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.features.entity.EntityHealthOverrides
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.types.FloatPredicate
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

@GenerateCodec
data class MaxHealthEntityCondition(
    @FieldName("max_health") @Compact val maxHealth: FloatPredicate,
    @FieldNames("use_modifiers") val useModifiers: Boolean = true,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = KatharsisCodecs.getMapCodec<MaxHealthEntityCondition>()

    override fun matches(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false
        return EntityHealthOverrides.doesHealthMatch(entity, maxHealth, useModifiers)
    }
}
