package me.owdding.catharsis.features.armor

import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.LivingEntityRenderEvent

class ArmorDefinitionRenderState {

    var head: ResourceLocation? = null
    var chest: ResourceLocation? = null
    var legs: ResourceLocation? = null
    var feet: ResourceLocation? = null
}

@Module
object ArmorDefinitionRenderStateHandler {

    @Subscription
    fun onExtractHumanoidState(event: LivingEntityRenderEvent) {
        val entity = event.entity ?: return
        val hook = event.state as? LivingEntityRenderStateHook ?: return
        val state = hook.`catharsis$getArmorDefinitionRenderState`()

        state.head = entity.resolveTexture(EquipmentSlot.HEAD)
        state.chest = entity.resolveTexture(EquipmentSlot.CHEST)
        state.legs = entity.resolveTexture(EquipmentSlot.LEGS)
        state.feet = entity.resolveTexture(EquipmentSlot.FEET)
    }

    private fun LivingEntity.resolveTexture(slot: EquipmentSlot): ResourceLocation? {
        val item = this.getItemBySlot(slot)
        val definition = ArmorDefinitions.getDefinition(item.getCatharsisId()) ?: ArmorDefinitions.getDefinition(item.get(DataComponents.ITEM_MODEL))
        return definition?.resolve(item, this, slot)
    }

    private fun ItemStack.getCatharsisId(): ResourceLocation? {
        val id = this.getData(DataTypes.API_ID) ?: return null
        return ResourceLocation.tryBuild("skyblock", id.replace(":", "-").lowercase())
    }
}
