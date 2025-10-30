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
import java.util.*

class ArmorDefinitionRenderState {

    var head: ResourceLocation? = null
    var chest: ResourceLocation? = null
    var legs: ResourceLocation? = null
    var feet: ResourceLocation? = null
    var hiddenStates: EnumMap<BodyPart, HiddenState>? = null
}

@Module
object ArmorDefinitionRenderStateHandler {

    @Subscription
    fun onExtractHumanoidState(event: LivingEntityRenderEvent) {
        val entity = event.entity ?: return
        val hook = event.state as? LivingEntityRenderStateHook ?: return
        val state = hook.`catharsis$getArmorDefinitionRenderState`()

        state.head = merge(state, entity.resolveTexture(EquipmentSlot.HEAD))
        state.chest = merge(state, entity.resolveTexture(EquipmentSlot.CHEST))
        state.legs = merge(state, entity.resolveTexture(EquipmentSlot.LEGS))
        state.feet = merge(state, entity.resolveTexture(EquipmentSlot.FEET))
    }

    fun merge(state: ArmorDefinitionRenderState, data: Pair<ResourceLocation, EnumMap<BodyPart, HiddenState>>?): ResourceLocation? {
        data ?: return null
        if (state.hiddenStates == null) {
            state.hiddenStates = EnumMap(BodyPart::class.java)
        }
        data.second.forEach { (key, value) ->
            state.hiddenStates?.computeIfPresent(key) { _, present ->
                if (present == value) value else HiddenState(present.overlay || value.overlay, present.base || value.overlay)
            }
            state.hiddenStates?.putIfAbsent(key, value)
        }
        return data.first
    }

    private fun LivingEntity.resolveTexture(slot: EquipmentSlot): Pair<ResourceLocation, EnumMap<BodyPart, HiddenState>>? {
        val item = this.getItemBySlot(slot)
        val definition = ArmorDefinitions.getDefinition(item.getCatharsisId()) ?: ArmorDefinitions.getDefinition(item.get(DataComponents.ITEM_MODEL))
        definition ?: return null
        return definition.resolve(item, this, slot) to definition.hiddenBodyParts
    }

    private fun ItemStack.getCatharsisId(): ResourceLocation? {
        val id = this.getData(DataTypes.API_ID) ?: return null
        return ResourceLocation.tryBuild("skyblock", id.replace(":", "-").lowercase())
    }
}
