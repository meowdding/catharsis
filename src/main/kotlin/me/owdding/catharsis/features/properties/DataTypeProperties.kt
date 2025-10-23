package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.isEnum
import me.owdding.catharsis.utils.extensions.isNumber
import me.owdding.catharsis.utils.extensions.set
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import java.util.function.Function

private typealias SelectType<Type> = SelectItemModelProperty.Type<out SelectItemModelProperty<Type>, Type>

@Module
object DataTypeProperties {
    private val conditionalTypes: ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out ConditionalItemModelProperty>> = ExtraCodecs.LateBoundIdMapper()
    private val numericalTypes: ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out RangeSelectItemModelProperty>> = ExtraCodecs.LateBoundIdMapper()
    private val selectTypes: ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out SelectItemModelProperty<*>>> = ExtraCodecs.LateBoundIdMapper()

    init {
        register(DataTypes.VISIBLE_ITEM)
    }

    private inline fun <reified Type> register(type: DataType<Type>) = register(Catharsis.sbapi(type.id), type)

    private inline fun <reified Type> register(location: ResourceLocation, type: DataType<Type>) {
        if (Type::class.isNumber || Type::class.isEnum) {
            val isEnum = Type::class.isEnum
            if (isEnum) {
                numericalTypes[location] = MapCodec.unit { RangeDataTypeItemProperty(type.unsafeCast<DataType<*>, DataType<out Enum<*>>>()) { it.ordinal } }
            } else {
                numericalTypes[location] = MapCodec.unit { RangeDataTypeItemProperty(type.unsafeCast<DataType<*>, DataType<out Number>>()) { it } }
            }
        }
        if (Type::class == Boolean::class) {
            conditionalTypes[location] = MapCodec.unit { ConditionalDataTypeItemProperty(type.unsafeCast()) }
        }
        selectTypes[location] = MapCodec.unit {
            SelectDataTypeItemProperty(type, CatharsisCodecs.getCodec(), SelectType.createCasesFieldCodec(CatharsisCodecs.getCodec()))
        }
    }

    @Subscription
    private fun BootstrapSelectPropertiesEvent.bootstrap() {
        register(
            Catharsis.id("data_type"),
            selectTypes.codec(ResourceLocation.CODEC).dispatchMap("data_type", { MapCodec.unit { it } }, Function.identity()),
        )
    }
    @Subscription
    private fun BootstrapNumericPropertiesEvent.bootstrap() {
        register(
            Catharsis.id("data_type"),
            numericalTypes.codec(ResourceLocation.CODEC).dispatchMap("data_type", { MapCodec.unit { it } }, Function.identity()),
        )
    }
    @Subscription
    private fun BootstrapConditionalPropertiesEvent.bootstrap() {
        register(
            Catharsis.id("data_type"),
            conditionalTypes.codec(ResourceLocation.CODEC).dispatchMap("data_type", { MapCodec.unit { it } }, Function.identity()),
        )
    }

    data class SelectDataTypeItemProperty<Type>(val dataType: DataType<Type>, val dataCodec: Codec<Type>, val type: SelectType<Type>) : SelectItemModelProperty<Type> {
        override fun get(
            stack: ItemStack,
            level: ClientLevel?,
            entity: LivingEntity?,
            seed: Int,
            displayContext: ItemDisplayContext,
        ): Type? = stack[dataType]

        override fun valueCodec(): Codec<Type> = dataCodec

        override fun type() = type
    }

    @GenerateCodec
    data class RangeDataTypeItemProperty<Type>(val dataType: DataType<Type>, val numberConverter: (Type) -> Number) : RangeSelectItemModelProperty {
        override fun get(
            stack: ItemStack,
            level: ClientLevel?,
            owner: ItemOwner?,
            seed: Int,
        ): Float = (stack[dataType]?.let(numberConverter)?.toFloat()) ?: 0f

        override fun type(): MapCodec<out RangeSelectItemModelProperty> = CatharsisCodecs.getMapCodec()
    }

    @GenerateCodec
    data class ConditionalDataTypeItemProperty(val dataType: DataType<Boolean>) : ConditionalItemModelProperty {
        override fun type(): MapCodec<out ConditionalItemModelProperty> = CatharsisCodecs.getMapCodec()
        override fun get(
            stack: ItemStack,
            level: ClientLevel?,
            entity: LivingEntity?,
            seed: Int,
            displayContext: ItemDisplayContext,
        ): Boolean = stack[dataType] == true
    }
}
