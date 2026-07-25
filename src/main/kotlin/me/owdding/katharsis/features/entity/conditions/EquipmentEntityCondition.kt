package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.hooks.armor.SelectItemModelPropertyTypeHook
import me.owdding.katharsis.utils.extensions.dispatchLenientMap
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext

@Suppress("UNCHECKED_CAST")
val <Property : SelectItemModelProperty<Type>, Type : Any> SelectItemModelProperty.Type<Property, Type>.equipmentHook: SelectItemModelPropertyTypeHook<Property, Type>
    get() = this as Any as SelectItemModelPropertyTypeHook<Property, Type>

@GenerateCodec
data class ConditionalEquipmentEntityCondition(
    val slot: EquipmentSlot,
    val property: ConditionalItemModelProperty,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = KatharsisCodecs.getMapCodec<ConditionalEquipmentEntityCondition>()
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

data class SelectEquipmentCase<Type>(val `when`: List<Type>, val result: Boolean)

class SelectEquipmentEntityConditionSwitch<Property : SelectItemModelProperty<Type>, Type : Any>(
    val property: Property,
    val cases: List<SelectEquipmentCase<Type>>,
) {
    companion object {
        val CODEC: MapCodec<SelectEquipmentEntityConditionSwitch<*, *>> = SelectItemModelProperties.CODEC.dispatchLenientMap(
            "property",
            { switch -> DataResult.success(switch.property.type()) },
            { type ->
                type.equipmentHook.`katharsis$getEquipmentSwitchCodec`()
                    ?.let(DataResult<SelectEquipmentEntityConditionSwitch<*, *>>::success)
                    ?: DataResult.error { "No codec for Equipment select property type: ${type::class.java}" }
            },
        )
    }
}

class SelectEquipmentEntityCondition<Property : SelectItemModelProperty<Type>, Type : Any>(
    val slot: EquipmentSlot,
    val switch: SelectEquipmentEntityConditionSwitch<Property, Type>,
    val fallback: Boolean,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> get() = CODEC
    override val cost: Int = 10

    override fun matches(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false
        val equipmentInSlot = entity.getItemBySlot(slot)
        val value = switch.property.get(equipmentInSlot, entity.level() as? ClientLevel, entity, entity.id, ItemDisplayContext.NONE) ?: return fallback

        for (case in switch.cases) {
            if (case.`when`.contains(value)) return case.result
        }
        return fallback
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val CODEC: MapCodec<SelectEquipmentEntityCondition<*, *>> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                EquipmentSlot.CODEC.fieldOf("slot").forGetter(SelectEquipmentEntityCondition<*, *>::slot),
                SelectEquipmentEntityConditionSwitch.CODEC.forGetter(SelectEquipmentEntityCondition<*, *>::switch),
                Codec.BOOL.optionalFieldOf("fallback", false).forGetter(SelectEquipmentEntityCondition<*, *>::fallback),
            ).apply(instance) { slot, switch, fallback ->
                SelectEquipmentEntityCondition(slot, switch as SelectEquipmentEntityConditionSwitch<SelectItemModelProperty<Any>, Any>, fallback)
            }
        } as MapCodec<SelectEquipmentEntityCondition<*, *>>
    }
}

data class RangeEquipmentEntry(val threshold: Float, val result: Boolean)

class RangeSelectEquipmentEntityCondition(
    val slot: EquipmentSlot,
    val property: RangeSelectItemModelProperty,
    val scale: Float,
    val entries: List<RangeEquipmentEntry>,
    val fallback: Boolean,
) : EntityCondition {
    private val sortedEntries = entries.sortedWith(Comparator.comparingDouble { it.threshold.toDouble() })
    private val thresholds = FloatArray(sortedEntries.size) { i -> sortedEntries[i].threshold }
    private val results = BooleanArray(sortedEntries.size) { i -> sortedEntries[i].result }

    override val codec: MapCodec<out EntityCondition> get() = CODEC
    override val cost: Int = 10

    private fun lastIndexLessThanOrEqual(value: Float): Int {
        if (thresholds.size < 16) {
            for (i in thresholds.indices) {
                if (thresholds[i] > value) {
                    return i - 1
                }
            }
            return thresholds.size - 1
        } else {
            val i = thresholds.binarySearch(value)
            return if (i < 0) i.inv() - 1 else i
        }
    }

    override fun matches(entity: Entity): Boolean {
        if (entity !is LivingEntity) return false
        val equipmentInSlot = entity.getItemBySlot(slot)
        val value = property.get(equipmentInSlot, entity.level() as? ClientLevel, entity, entity.id) * scale

        if (value.isNaN()) return fallback
        val index = lastIndexLessThanOrEqual(value)
        return if (index >= 0 && index < results.size) results[index] else fallback
    }

    companion object {
        val ENTRY_CODEC: Codec<RangeEquipmentEntry> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("threshold").forGetter(RangeEquipmentEntry::threshold),
                Codec.BOOL.fieldOf("result").forGetter(RangeEquipmentEntry::result),
            ).apply(instance) { t, r -> RangeEquipmentEntry(t, r) }
        }

        val CODEC: MapCodec<RangeSelectEquipmentEntityCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                EquipmentSlot.CODEC.fieldOf("slot").forGetter(RangeSelectEquipmentEntityCondition::slot),
                RangeSelectItemModelProperties.MAP_CODEC.forGetter(RangeSelectEquipmentEntityCondition::property),
                Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(RangeSelectEquipmentEntityCondition::scale),
                ENTRY_CODEC.listOf().fieldOf("entries").forGetter(RangeSelectEquipmentEntityCondition::entries),
                Codec.BOOL.optionalFieldOf("fallback", false).forGetter(RangeSelectEquipmentEntityCondition::fallback),
            ).apply(instance) { slot, property, scale, entries, fallback ->
                RangeSelectEquipmentEntityCondition(slot, property, scale, entries, fallback)
            }
        }
    }
}
