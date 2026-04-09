package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.armor.models.SelectArmorModel
import me.owdding.catharsis.features.armor.models.hook
import me.owdding.catharsis.features.tooltip.models.SelectTooltipDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.generated.CodecUtils
import me.owdding.catharsis.generated.EnumCodec
import me.owdding.catharsis.utils.extensions.isEnum
import me.owdding.catharsis.utils.extensions.isNumber
import me.owdding.catharsis.utils.extensions.set
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.SelectItemModel
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
import tech.thatgravyboat.skyblockapi.api.datatype.getDataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import java.util.function.Function
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

data class DataTypeEntry<Type, CompareType>(val type: DataType<Type>, val codec: Codec<CompareType>, val converter: Function<Type, CompareType>)
data class NumbericalDataTypeEntry<Type, CompareType : Number>(val type: DataType<Type>, val converter: Function<Type, CompareType>, val codec: Codec<CompareType>)

@Module
object DataTypeProperties {

    val ID = Catharsis.id("data_type")

    val conditionalTypes: ExtraCodecs.LateBoundIdMapper<String, DataType<Boolean>> = ExtraCodecs.LateBoundIdMapper()
    val numericalTypes: ExtraCodecs.LateBoundIdMapper<String, NumbericalDataTypeEntry<*, *>> = ExtraCodecs.LateBoundIdMapper()
    val stringTypes: ExtraCodecs.LateBoundIdMapper<String, DataTypeEntry<*, *>> = ExtraCodecs.LateBoundIdMapper()
    val allTypes: ExtraCodecs.LateBoundIdMapper<String, DataType<*>> = ExtraCodecs.LateBoundIdMapper()

    init {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val dataTypes = DataTypesRegistry.types
        dataTypes.filterType<Boolean>().forEach(::register)
        dataTypes.filterType<String>().forEach(::register)
        dataTypes.filterType<Int>().forEach(::register)
        dataTypes.filterType<Long>().forEach(::register)
        dataTypes.filterType<Short>().forEach(::register)
        dataTypes.filterType<Double>().forEach(::register)
        dataTypes.filterType<Float>().forEach(::register)

        dataTypes.filterType<SkyBlockId>().forEach {
            register(it, Codec.STRING) { id -> id.skyblockId }
        }

        register(DataTypes.RARITY)
        register(DataTypes.HOOK, Codec.STRING, Pair<*, String>::second)
        register(DataTypes.LINE, Codec.STRING, Pair<*, String>::second)
        register(DataTypes.SINKER, Codec.STRING, Pair<*, String>::second)
        register(DataTypes.FUEL, Codec.INT, Pair<Int, *>::first)
        register(DataTypes.SNOWBALLS, Codec.INT, Pair<Int, *>::first)
        register(DataTypes.DUNGEONBREAKER_CHARGES, Codec.INT, Pair<Int, *>::first)
        register(DataTypes.WATER_LEVEL, Codec.INT, Pair<Int, *>::first)
        register(DataTypes.UUID, CodecUtils.UUID_CODEC)
        dataTypes.forEach { allTypes[it.id] = it }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private inline fun <reified T : Any> List<DataType<*>>.filterType(): List<DataType<T>> {
        val type = typeOf<T>().javaType
        @Suppress("UNCHECKED_CAST")
        return filter { it.type?.javaType == type }.unsafeCast<List<DataType<T>>>()
    }

    @JvmName("registerEnum")
    private inline fun <reified Type : Enum<Type>> register(type: DataType<Type>) = register(type, EnumCodec.forKCodec(Type::class.java.enumConstants))

    @JvmName("registerInt")
    private fun register(type: DataType<Int>) = register(type, CatharsisCodecs.getCodec())

    @JvmName("registerFloat")
    private fun register(type: DataType<Float>) = register(type, CatharsisCodecs.getCodec())

    @JvmName("registerDouble")
    private fun register(type: DataType<Double>) = register(type, CatharsisCodecs.getCodec())

    @JvmName("registerLong")
    private fun register(type: DataType<Long>) = register(type, CatharsisCodecs.getCodec())

    @JvmName("registerShort")
    private fun register(type: DataType<Short>) = register(type, CatharsisCodecs.getCodec())

    @JvmName("registerString")
    private fun register(type: DataType<String>) = register(type, Codec.STRING)

    @JvmName("registerBoolean")
    private fun register(type: DataType<Boolean>) = register(type, Codec.BOOL)

    private inline fun <reified Type> register(type: DataType<Type>, codec: Codec<Type>) = register(type.id, type, codec)
    private inline fun <reified Type, reified CompareType> register(type: DataType<Type>, codec: Codec<CompareType>, converter: Function<Type, CompareType>) =
        register(type.id, type, codec, converter)

    private inline fun <reified Type> register(location: String, type: DataType<Type>, codec: Codec<Type>) = register(location, type, codec, Function.identity())
    private inline fun <reified Type, reified CompareType> register(location: String, type: DataType<Type>, codec: Codec<CompareType>, converter: Function<Type, CompareType>) {
        stringTypes[location] = DataTypeEntry(type, codec, converter)
        if (CompareType::class.isNumber || CompareType::class.isEnum) {
            numericalTypes[location] = NumbericalDataTypeEntry(
                type,
                if (CompareType::class.isEnum) {
                    Function { value: Type ->
                        (converter.apply(value) as Enum<*>).ordinal
                    }
                } else converter.unsafeCast(),
                Codec.FLOAT.xmap({it as Number}, { it.toFloat() })
            )
        }
        if (CompareType::class == Boolean::class) {
            conditionalTypes[location] = type.unsafeCast()
        }
    }

    data class SelectDataTypeItemProperty<Type, CompareType : Any>(val entry: DataTypeEntry<Type, CompareType>) : SelectItemModelProperty<CompareType> {
        override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): CompareType? =
            stack[entry.type]?.let { entry.converter.apply(it) }

        override fun valueCodec(): Codec<CompareType> = entry.codec
        override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<CompareType>, CompareType> = TYPE.unsafeCast()

        companion object {

            private fun <Type, CompareType : Any> createItemCodec(entry: DataTypeEntry<Type, CompareType>): MapCodec<SelectItemModel.UnbakedSwitch<SelectDataTypeItemProperty<Type, CompareType>, CompareType>> {
                return SelectItemModelProperty.Type.createCasesFieldCodec(entry.codec).xmap(
                    { cases -> SelectItemModel.UnbakedSwitch(SelectDataTypeItemProperty(entry), cases) },
                    { switch -> switch.cases },
                )
            }

            private fun <Type, CompareType : Any> createArmorCodec(entry: DataTypeEntry<Type, CompareType>): MapCodec<SelectArmorModel.UnbakedSwitch<SelectDataTypeItemProperty<Type, CompareType>, CompareType>> {
                return SelectArmorModel.UnbakedSwitch.createCasesFieldCodec(entry.codec).xmap(
                    { cases -> SelectArmorModel.UnbakedSwitch(SelectDataTypeItemProperty(entry), cases) },
                    { switch -> switch.cases },
                )
            }

            private fun <Type, CompareType : Any> createTooltipCodec(entry: DataTypeEntry<Type, CompareType>): MapCodec<SelectTooltipDefinition.UnbakedSwitch<SelectDataTypeItemProperty<Type, CompareType>, CompareType>> {
                return SelectTooltipDefinition.UnbakedSwitch.createCasesFieldCodec(entry.codec).xmap(
                    { cases -> SelectTooltipDefinition.UnbakedSwitch(SelectDataTypeItemProperty(entry), cases) },
                    { switch -> switch.cases },
                )
            }

            private fun <Type, CompareType : Any> createType(): SelectItemModelProperty.Type<SelectDataTypeItemProperty<Type, CompareType>, CompareType> {
                val type = SelectItemModelProperty.Type<SelectDataTypeItemProperty<Type, CompareType>, CompareType>(
                    stringTypes.codec(Codec.STRING).dispatchMap(
                        "data_type",
                        { case -> (case.property as SelectDataTypeItemProperty).entry },
                        { entry -> createItemCodec(entry.unsafeCast()) },
                    ),
                )
                type.hook.`catharsis$setArmorSwitchCodec`(
                    stringTypes.codec(Codec.STRING).dispatchMap(
                        "data_type",
                        { case -> (case.property as SelectDataTypeItemProperty).entry },
                        { entry -> createArmorCodec(entry.unsafeCast()) },
                    ),
                )
                type.hook.`catharsis$setTooltipSwitchCodec`(
                    stringTypes.codec(Codec.STRING).dispatchMap(
                        "data_type",
                        { case -> (case.property as SelectDataTypeItemProperty).entry },
                        { entry -> createTooltipCodec(entry.unsafeCast()) },
                    ),
                )
                return type
            }

            val TYPE: SelectItemModelProperty.Type<SelectDataTypeItemProperty<Any, Any>, Any> = createType<Any, Any>()
        }
    }

    data class RangeDataTypeItemProperty<Type, CompareType : Number>(val type: NumbericalDataTypeEntry<Type, CompareType>) : RangeSelectItemModelProperty {
        override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
            return type.converter.apply(stack[type.type] ?: return 0f).toFloat()
        }

        override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC

        companion object {
            val CODEC: MapCodec<RangeDataTypeItemProperty<*, *>> = numericalTypes.codec(Codec.STRING).fieldOf("data_type").xmap(
                { dataType -> RangeDataTypeItemProperty(dataType) },
                { property -> property.type },
            )
        }
    }

    data class ConditionalDataTypeItemProperty(val type: DataType<Boolean>) : ConditionalItemModelProperty {
        override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC
        override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean = stack[type] == true

        companion object {
            val CODEC: MapCodec<ConditionalDataTypeItemProperty> = conditionalTypes.codec(Codec.STRING).fieldOf("data_type").xmap(
                { dataType -> ConditionalDataTypeItemProperty(dataType) },
                { property -> property.type },
            )
        }
    }

    data class DataTypePresentItemProperty(val type: DataType<*>) : ConditionalItemModelProperty {
        companion object {
            val CODEC: MapCodec<DataTypePresentItemProperty> = allTypes.codec(Codec.STRING).fieldOf("data_type").xmap(
                { dataType -> DataTypePresentItemProperty(dataType) },
                { property -> property.type },
            )
        }

        override fun type(): MapCodec<out ConditionalItemModelProperty> = CODEC

        override fun get(
            stack: ItemStack,
            level: ClientLevel?,
            entity: LivingEntity?,
            seed: Int,
            displayContext: ItemDisplayContext,
        ): Boolean = stack.getDataTypes().containsKey(type)

    }
}
