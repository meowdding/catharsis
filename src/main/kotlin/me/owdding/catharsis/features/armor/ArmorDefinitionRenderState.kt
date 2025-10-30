package me.owdding.catharsis.features.armor

import me.owdding.catharsis.features.armor.models.ArmorModelState
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook
import me.owdding.catharsis.utils.ItemUtils
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.LivingEntityRenderEvent
import java.util.EnumMap
import java.util.*

class ArmorDefinitionRenderState {

    var head: ArmorModelState? = null
    var chest: ArmorModelState? = null
    var legs: ArmorModelState? = null
    var feet: ArmorModelState? = null

    var hiddenStates: EnumMap<BodyPart, HiddenState>? = null

    fun fromSlot(slot: EquipmentSlot) = when (slot) {
        EquipmentSlot.HEAD -> this.head
        EquipmentSlot.CHEST -> this.chest
        EquipmentSlot.LEGS -> this.legs
        EquipmentSlot.FEET -> this.feet
        else -> null
    }
}

@Module
object ArmorDefinitionRenderStateHandler {

    @Subscription
    fun onExtractHumanoidState(event: LivingEntityRenderEvent) {
        val entity = event.entity ?: return
        val hook = event.state as? LivingEntityRenderStateHook ?: return
        val state = hook.`catharsis$getArmorDefinitionRenderState`()

        state.head = merge(state, entity.resolveRenderer(EquipmentSlot.HEAD))
        state.chest = merge(state, entity.resolveRenderer(EquipmentSlot.CHEST))
        state.legs = merge(state, entity.resolveRenderer(EquipmentSlot.LEGS))
        state.feet = merge(state, entity.resolveRenderer(EquipmentSlot.FEET))
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
    private fun LivingEntity.resolveRenderer(slot: EquipmentSlot): ArmorModelState? {
        val item = this.getItemBySlot(slot)
        val definition = ArmorDefinitions.getDefinition(ItemUtils.getCustomLocation(item)) ?: ArmorDefinitions.getDefinition(item.get(DataComponents.ITEM_MODEL))
        definition ?: return null
        return definition.resolve(item, this, slot) to definition.hiddenBodyParts
    }
}
