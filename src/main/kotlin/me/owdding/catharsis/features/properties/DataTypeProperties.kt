//~ item_holder
package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
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
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import java.util.*
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

data class DataTypeEntry<Type>(val type: DataType<Type>, val codec: Codec<Type>)

@Module
object DataTypeProperties {

    val ID = Catharsis.id("data_type")

    private val conditionalTypes: ExtraCodecs.LateBoundIdMapper<String, DataType<Boolean>> = ExtraCodecs.LateBoundIdMapper()
    private val numericalTypes: ExtraCodecs.LateBoundIdMapper<String, DataType<*>> = ExtraCodecs.LateBoundIdMapper()
    private val types: ExtraCodecs.LateBoundIdMapper<String, DataTypeEntry<*>> = ExtraCodecs.LateBoundIdMapper()

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

        register(DataTypes.RARITY)
        register(DataTypes.HOOK, Codec.STRING.xmap({ UUID.randomUUID() to it }, { it.second }))
        register(DataTypes.LINE, Codec.STRING.xmap({ UUID.randomUUID() to it }, { it.second }))
        register(DataTypes.SINKER, Codec.STRING.xmap({ UUID.randomUUID() to it }, { it.second }))
        register(DataTypes.FUEL, Codec.INT.xmap({ it to it }, { it.first }))
        register(DataTypes.SNOWBALLS, Codec.INT.xmap({ it to it }, { it.first }))
        register(DataTypes.UUID, CodecUtils.UUID_CODEC)
        register(DataTypes.DUNGEONBREAKER_CHARGES, Codec.INT.xmap({ it to it }, { it.first }))
    }

    @OptIn(ExperimentalStdlibApi::class)
    private inline fun <reified T : Any> List<DataType<*>>.filterType(): List<DataType<T>> {
        val type = typeOf<T>().javaType
        @Suppress("UNCHECKED_CAST")
        return filter {
            it.type?.javaType == type
        }.unsafeCast<List<DataType<T>>>()
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

    private inline fun <reified Type> register(location: String, type: DataType<Type>, codec: Codec<Type>) {
        types[location] = DataTypeEntry(type, codec)
        if (Type::class.isNumber || Type::class.isEnum) {
            numericalTypes[location] = type
        }
        if (Type::class == Boolean::class) {
            conditionalTypes[location] = type.unsafeCast()
        }
    }

    data class SelectDataTypeItemProperty<Type>(val entry: DataTypeEntry<Type>) : SelectItemModelProperty<Type> {
        override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): Type? = stack[entry.type]
        override fun valueCodec(): Codec<Type> = entry.codec
        override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<Type>, Type> = TYPE.unsafeCast()

        companion object {

            private fun <Type> createCodec(entry: DataTypeEntry<Type>): MapCodec<SelectItemModel.UnbakedSwitch<SelectDataTypeItemProperty<Type>, Type>> {
                return SelectItemModelProperty.Type.createCasesFieldCodec(entry.codec).xmap(
                    { cases -> SelectItemModel.UnbakedSwitch(SelectDataTypeItemProperty(entry), cases) },
                    { switch -> switch.cases },
                )
            }

            private fun <Type> createType(): SelectItemModelProperty.Type<SelectDataTypeItemProperty<Type>, Type> = SelectItemModelProperty.Type(
                types.codec(Codec.STRING).dispatchMap(
                    "data_type",
                    { case -> (case.property as SelectDataTypeItemProperty).entry },
                    { entry -> createCodec(entry).unsafeCast() },
                ),
            )

            val TYPE: SelectItemModelProperty.Type<SelectDataTypeItemProperty<Any>, Any> = createType<Any>()
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
}
