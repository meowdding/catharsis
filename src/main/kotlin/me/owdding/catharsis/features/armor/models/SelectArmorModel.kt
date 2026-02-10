package me.owdding.catharsis.features.armor.models

import com.google.common.collect.HashMultiset
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import me.owdding.catharsis.hooks.armor.SelectItemModelPropertyTypeHook
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.codecs.VersionedCodecs.dispatchLenientMap
import net.minecraft.client.multiplayer.CacheSlot
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

typealias UnbakedArmorSelectCase<Type> = Pair<List<Type>, ArmorModel.Unbaked>

@Suppress("UNCHECKED_CAST", "KotlinConstantConditions")
val <Property : SelectItemModelProperty<Type>, Type : Any> SelectItemModelProperty.Type<Property, Type>.hook: SelectItemModelPropertyTypeHook<Property, Type>
    get() = this as Any as SelectItemModelPropertyTypeHook<Property, Type>

class SelectArmorModel<Type : Any>(
    private val property: SelectItemModelProperty<Type>,
    private val models: (Type?, ClientLevel?) -> ArmorModel?,
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        val value = this.property.get(stack, level, owner?.asLivingEntity(), seed, ItemDisplayContext.NONE)
        return this.models.invoke(value, level)?.resolve(stack, level, owner, seed) ?: ArmorModelState.Missing
    }

    class Unbaked(
        val switch: UnbakedSwitch<*, *>,
        val fallback: Optional<ArmorModel.Unbaked>,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            return switch.bake(swapper, resources, fallback.map { it.bake(swapper, resources) }.getOrNull())
        }

        companion object {

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec {
                it.group(
                    UnbakedSwitch.CODEC.forGetter(Unbaked::switch),
                    ArmorModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback),
                ).apply(it, ::Unbaked)
            }
        }
    }

    class UnbakedSwitch<Property : SelectItemModelProperty<Type>, Type : Any>(val property: Property, val cases: List<UnbakedArmorSelectCase<Type>>) {

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager, fallback: ArmorModel?): ArmorModel {
            val lookup = Object2ObjectOpenHashMap<Type, ArmorModel>()
            for ((types, model) in cases) {
                val bakedModel = model.bake(swapper, resources)
                for (type in types) {
                    lookup[type] = bakedModel
                }
            }
            lookup.defaultReturnValue(fallback)

            return if (swapper == null) {
                SelectArmorModel(property) { type, _ -> lookup[type] }
            } else {
                val cache = CacheSlot<ClientLevel, Object2ObjectMap<Type, ArmorModel>> { level ->
                    val cachedLookup = Object2ObjectOpenHashMap<Type, ArmorModel>(lookup.size)
                    cachedLookup.defaultReturnValue(fallback)
                    lookup.forEach { (type, model) ->
                        if (type == null) return@forEach
                        swapper.swapTo(property.valueCodec(), type, level.registryAccess()).ifSuccess {
                            cachedLookup[type] = model
                        }
                    }
                    cachedLookup
                }

                SelectArmorModel(property) { type, level ->
                    when {
                        level == null -> lookup[type]
                        type == null -> fallback
                        else -> cache.compute(level)[type]
                    }
                }
            }
        }

        companion object {

            val CODEC: MapCodec<UnbakedSwitch<*, *>> = SelectItemModelProperties.CODEC.dispatchLenientMap(
                "property",
                { switch -> DataResult.success(switch.property.type()) },
                { type ->
                    type.hook.`catharsis$getArmorSwitchCodec`()
                        ?.let(DataResult<UnbakedSwitch<*, *>>::success)
                        ?: DataResult.error { "No codec for armor select property type: ${type::class.java}" }
                },
            )

            @JvmStatic
            fun <Type> createCasesFieldCodec(codec: Codec<Type>): MapCodec<List<UnbakedArmorSelectCase<Type>>> {
                val casesCodec: Codec<List<UnbakedArmorSelectCase<Type>>> = RecordCodecBuilder.create {
                    it.group(
                        ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(codec)).fieldOf("when").forGetter(UnbakedArmorSelectCase<Type>::first),
                        ArmorModels.CODEC.fieldOf("model").forGetter(UnbakedArmorSelectCase<Type>::second),
                    ).apply(it, ::Pair)
                }.listOf()

                val validatedCodec: Codec<List<UnbakedArmorSelectCase<Type>>> = casesCodec.validate { cases ->
                    if (cases.isEmpty()) {
                        DataResult.error { "Empty case list" }
                    } else {
                        val sets = HashMultiset.create<Type>(cases.flatMap(UnbakedArmorSelectCase<Type>::first))

                        if (sets.size != sets.entrySet().size) {
                            val duplicateCases = sets.entrySet().filter { it.count > 1 }.map { it.element.toString() }
                            DataResult.error { "Duplicate case conditions: ${duplicateCases.joinToString(", ")}" }
                        } else {
                            DataResult.success(cases)
                        }
                    }
                }

                return validatedCodec.fieldOf("cases")
            }
        }
    }
}
