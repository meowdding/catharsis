package me.owdding.catharsis.features.armor

import me.owdding.catharsis.features.armor.models.ArmorModel
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient

data class ArmorDefinition(
    val model: ArmorModel,
) {

    fun resolve(stack: ItemStack, entity: LivingEntity?, slot: EquipmentSlot): ResourceLocation {
        return model.resolve(stack, McClient.self.level, entity, slot.ordinal + (entity?.id ?: 0))
    }

    @GenerateCodec
    data class Unbaked(
        val model: ArmorModel.Unbaked,
    ) {

        fun bake(swapper: RegistryContextSwapper?): ArmorDefinition {
            return ArmorDefinition(model.bake(swapper))
        }
    }
}
