package me.owdding.catharsis.features.tooltip.models

import com.google.common.collect.HashMultiset
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import me.owdding.catharsis.features.tooltip.TooltipDefinition
import me.owdding.catharsis.features.tooltip.TooltipDefinitions
import me.owdding.catharsis.hooks.armor.SelectItemModelPropertyTypeHook
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.codecs.VersionedCodecs.dispatchLenientMap
import net.minecraft.client.multiplayer.CacheSlot
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

typealias UnbakedTooltipSelectCase<Type> = Pair<List<Type>, TooltipDefinition.Unbaked>

@Suppress("UNCHECKED_CAST")
val <Property : SelectItemModelProperty<Type>, Type : Any> SelectItemModelProperty.Type<Property, Type>.hook: SelectItemModelPropertyTypeHook<Property, Type>
    get() = this as Any as SelectItemModelPropertyTypeHook<Property, Type>

data class SelectTooltipDefinition<Type : Any>(
    private val property: SelectItemModelProperty<Type>,
    private val models: (Type?, ClientLevel?) -> TooltipDefinition?,
) : TooltipDefinition {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?): Identifier? {
        val value = this.property.get(stack, level, owner?.asLivingEntity(), 0, ItemDisplayContext.NONE)
        return this.models.invoke(value, level)?.resolve(stack, level, owner)
    }

    class Unbaked(
        val switch: UnbakedSwitch<*, *>,
        val fallback: Optional<TooltipDefinition.Unbaked>,
    ) : TooltipDefinition.Unbaked {

        override val codec: MapCodec<out TooltipDefinition.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipDefinition {
            return switch.bake(swapper, resources, fallback.map { it.bake(swapper, resources) }.getOrNull())
        }

        companion object {

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec {
                it.group(
                    UnbakedSwitch.CODEC.forGetter(Unbaked::switch),
                    TooltipDefinitions.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback),
                ).apply(it, ::Unbaked)
            }
        }
    }

    class UnbakedSwitch<Property : SelectItemModelProperty<Type>, Type : Any>(val property: Property, val cases: List<UnbakedTooltipSelectCase<Type>>) {

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager, fallback: TooltipDefinition?): TooltipDefinition {
            val lookup = Object2ObjectOpenHashMap<Type, TooltipDefinition>()
            for ((types, model) in cases) {
                val bakedModel = model.bake(swapper, resources)
                for (type in types) {
                    lookup[type] = bakedModel
                }
            }
            lookup.defaultReturnValue(fallback)

            return if (swapper == null) {
                SelectTooltipDefinition(property) { type, _ -> lookup[type] }
            } else {
                val cache = CacheSlot<ClientLevel, Object2ObjectMap<Type, TooltipDefinition>> { level ->
                    val cachedLookup = Object2ObjectOpenHashMap<Type, TooltipDefinition>(lookup.size)
                    cachedLookup.defaultReturnValue(fallback)
                    lookup.forEach { (type, model) ->
                        if (type == null) return@forEach
                        swapper.swapTo(property.valueCodec(), type, level.registryAccess()).ifSuccess {
                            cachedLookup[type] = model
                        }
                    }
                    cachedLookup
                }

                SelectTooltipDefinition(property) { type, level ->
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
                    type.hook.`catharsis$getTooltipSwitchCodec`()
                        ?.let(DataResult<UnbakedSwitch<*, *>>::success)
                        ?: DataResult.error { "No codec for Tooltip select property type: ${type::class.java}" }
                },
            )

            @JvmStatic
            fun <Type> createCasesFieldCodec(codec: Codec<Type>): MapCodec<List<UnbakedTooltipSelectCase<Type>>> {
                val casesCodec: Codec<List<UnbakedTooltipSelectCase<Type>>> = RecordCodecBuilder.create {
                    it.group(
                        ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(codec)).fieldOf("when").forGetter(UnbakedTooltipSelectCase<Type>::first),
                        TooltipDefinitions.CODEC.fieldOf("model").forGetter(UnbakedTooltipSelectCase<Type>::second),
                    ).apply(it, ::Pair)
                }.listOf()

                val validatedCodec: Codec<List<UnbakedTooltipSelectCase<Type>>> = casesCodec.validate { cases ->
                    if (cases.isEmpty()) {
                        DataResult.error { "Empty case list" }
                    } else {
                        val sets = HashMultiset.create(cases.flatMap(UnbakedTooltipSelectCase<Type>::first))

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
