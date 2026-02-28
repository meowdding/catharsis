package me.owdding.catharsis.features.tooltip.models

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.tooltip.TooltipDefinition
import me.owdding.catharsis.features.tooltip.TooltipDefinitions
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.extensions.createCacheSlot
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest
import net.minecraft.resources.Identifier
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class ConditionalTooltipDefinition(
    private val property: ItemModelPropertyTest,
    private val onTrue: TooltipDefinition,
    private val onFalse: TooltipDefinition,
) : TooltipDefinition {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?): Identifier? {
        return if (property.get(stack, level, owner?.asLivingEntity(), 0, ItemDisplayContext.NONE)) {
            onTrue.resolve(stack, level, owner)
        } else {
            onFalse.resolve(stack, level, owner)
        }
    }

    class Unbaked(
        val property: ConditionalItemModelProperty,
        val onTrue: TooltipDefinition.Unbaked,
        val onFalse: TooltipDefinition.Unbaked,
    ) : TooltipDefinition.Unbaked {

        override val codec: MapCodec<out TooltipDefinition.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipDefinition {
            if (swapper == null) {
                return ConditionalTooltipDefinition(property, onTrue.bake(null, resources), onFalse.bake(null, resources))
            }
            val slot = createCacheSlot(swapper, property, ConditionalItemModelProperty::type)

            return ConditionalTooltipDefinition(
                { stack, level, owner, seed, context -> (level?.let(slot::compute) ?: property).get(stack, level, owner, seed, context) },
                onTrue.bake(swapper, resources),
                onFalse.bake(swapper, resources),
            )
        }

        companion object {

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec {
                it.group(
                    ConditionalItemModelProperties.MAP_CODEC.forGetter(Unbaked::property),
                    TooltipDefinitions.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue),
                    TooltipDefinitions.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse),
                ).apply(it, ::Unbaked)
            }
        }
    }
}
