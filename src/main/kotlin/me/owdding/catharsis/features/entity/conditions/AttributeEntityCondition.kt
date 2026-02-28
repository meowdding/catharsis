package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.types.FloatPredicate
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.Holder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import tech.thatgravyboat.skyblockapi.utils.extentions.serverValue

@GenerateCodec
data class AttributeEntityCondition(
    val attribute: Holder<Attribute>,
    @FieldNames("values", "value") @Compact val values: FloatPredicate,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<AttributeEntityCondition>()

    override fun matches(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false

        val attributeInstance = entity.getAttribute(attribute) ?: return false

        val attributeValue = attributeInstance.serverValue

        return values.contains(attributeValue)
    }
}
