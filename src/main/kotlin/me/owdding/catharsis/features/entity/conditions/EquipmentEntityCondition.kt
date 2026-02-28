package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext

@GenerateCodec
data class EquipmentEntityCondition(
    val slot: EquipmentSlot,
    val property: ConditionalItemModelProperty,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<EquipmentEntityCondition>()
    override val cost: Int = 10

    override fun matches(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false

        val equipmentInSlot = entity.getItemBySlot(slot)

        return property.get(
            equipmentInSlot,
            entity.level() as? ClientLevel,
            entity,
            entity.id,
            ItemDisplayContext.NONE,
        )
    }
}
