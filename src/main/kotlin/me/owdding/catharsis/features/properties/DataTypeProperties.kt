package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.isEnum
import me.owdding.catharsis.utils.extensions.isNumber
import me.owdding.catharsis.utils.extensions.set
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.extentions.get

@Module
object DataTypeProperties {

    val ID = Catharsis.id("data_type")

    private val conditionalTypes: ExtraCodecs.LateBoundIdMapper<String, DataType<Boolean>> = ExtraCodecs.LateBoundIdMapper()
    private val numericalTypes: ExtraCodecs.LateBoundIdMapper<String, DataType<*>> = ExtraCodecs.LateBoundIdMapper()
    private val types: ExtraCodecs.LateBoundIdMapper<String, DataType<*>> = ExtraCodecs.LateBoundIdMapper()

    init {
        // TODO needs to be automatically registered, probably ksp?
        register(DataTypes.RARITY)
    }

    private inline fun <reified Type> register(type: DataType<Type>) = register(type.id, type)

    private inline fun <reified Type> register(location: String, type: DataType<Type>) {
        types[location] = type
        if (Type::class.isNumber || Type::class.isEnum) {
            numericalTypes[location] = type
        }
        if (Type::class == Boolean::class) {
            @Suppress("UNCHECKED_CAST")
            conditionalTypes[location] = type as DataType<Boolean>
        }
    }

    data class SelectDataTypeItemProperty<Type>(val type: DataType<Type>) : SelectItemModelProperty<String> {
        override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): String {
            val value = stack[type] ?: return ""
            return when {
                value is Enum<*> -> value.name.lowercase()
                else -> value.toString().lowercase()
            }
        }

        override fun valueCodec(): Codec<String> = Codec.STRING
        override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = TYPE

        companion object {
            val CODEC: MapCodec<SelectDataTypeItemProperty<*>> = types.codec(Codec.STRING).fieldOf("data_type").xmap(
                { dataType -> SelectDataTypeItemProperty(dataType) },
                { property -> property.type }
            )
            val TYPE: SelectItemModelProperty.Type<SelectDataTypeItemProperty<*>, String> = SelectItemModelProperty.Type.create(CODEC, Codec.STRING)
        }
    }

    data class RangeDataTypeItemProperty<Type>(val type: DataType<Type>) : RangeSelectItemModelProperty {
        override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
            val value = stack[type] ?: return 0f
            return when (value) {
                is Number -> value.toFloat()
                is Enum<*> -> value.ordinal.toFloat()
                else -> 0f
            }
        }

        override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC

        companion object {
            val CODEC: MapCodec<RangeDataTypeItemProperty<*>> = numericalTypes.codec(Codec.STRING).fieldOf("data_type").xmap(
                { dataType -> RangeDataTypeItemProperty(dataType) },
                { property -> property.type }
            )
        }
    }

    data class ConditionalDataTypeItemProperty(val type: DataType<Boolean>) : ConditionalItemModelProperty {
        override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC
        override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean = stack[type] == true

        companion object {
            val CODEC: MapCodec<ConditionalDataTypeItemProperty> = conditionalTypes.codec(Codec.STRING).fieldOf("data_type").xmap(
                { dataType -> ConditionalDataTypeItemProperty(dataType) },
                { property -> property.type }
            )
        }
    }
}
