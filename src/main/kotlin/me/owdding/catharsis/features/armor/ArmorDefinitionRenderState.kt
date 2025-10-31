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
import java.util.*

class ArmorDefinitionRenderState {

    var head: ArmorModelState? = null
    var chest: ArmorModelState? = null
    var legs: ArmorModelState? = null
    var feet: ArmorModelState? = null

    var partVisibility: EnumMap<BodyPart, PartVisibilityState> = EnumMap(BodyPart::class.java)

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

        state.partVisibility.clear()
        state.updateState(entity, EquipmentSlot.HEAD) { def, armor -> def.head = armor }
        state.updateState(entity, EquipmentSlot.CHEST) { def, armor -> def.chest = armor }
        state.updateState(entity, EquipmentSlot.LEGS) { def, armor -> def.legs = armor }
        state.updateState(entity, EquipmentSlot.FEET) { def, armor -> def.feet = armor }
    }

    private fun ArmorDefinitionRenderState.updateState(entity: LivingEntity, slot: EquipmentSlot, updater: (ArmorDefinitionRenderState, ArmorModelState?) -> Unit) {
        val item = entity.getItemBySlot(slot)
        val definition = ArmorDefinitions.getDefinition(ItemUtils.getCustomLocation(item)) ?: ArmorDefinitions.getDefinition(item.get(DataComponents.ITEM_MODEL))
        updater.invoke(this, definition?.resolve(item, entity, slot))

        definition?.partVisibility?.forEach { (part, state) ->
            this.partVisibility.compute(part) { _, existing ->
                when {
                    existing == null -> state
                    else -> PartVisibilityState(
                        overlay = existing.overlay || state.overlay,
                        base = existing.base || state.base,
                    )
                }
            }
        }
    }
}
