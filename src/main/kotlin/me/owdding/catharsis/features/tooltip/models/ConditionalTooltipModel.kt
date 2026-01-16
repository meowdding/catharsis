package me.owdding.catharsis.features.tooltip.models

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.tooltip.TooltipModel
import me.owdding.catharsis.features.tooltip.TooltipModelState
import me.owdding.catharsis.features.tooltip.TooltipModels
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.extensions.createCacheSlot
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

//? = 1.21.8
/*import me.owdding.catharsis.utils.extensions.asLivingEntity*/

class ConditionalTooltipModel(
    private val property: ItemModelPropertyTest,
    private val onTrue: TooltipModel,
    private val onFalse: TooltipModel,
) : TooltipModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): TooltipModelState? {
        return if (property.get(stack, level, owner?.asLivingEntity(), seed, ItemDisplayContext.NONE)) {
            onTrue.resolve(stack, level, owner, seed)
        } else {
            onFalse.resolve(stack, level, owner, seed)
        }
    }

    override fun collectAll(): List<TooltipModelState> = buildList {
        addAll(onTrue.collectAll())
        addAll(onFalse.collectAll())
    }

    class Unbaked(
        val property: ConditionalItemModelProperty,
        val onTrue: TooltipModel.Unbaked,
        val onFalse: TooltipModel.Unbaked,
    ) : TooltipModel.Unbaked {

        override val codec: MapCodec<out TooltipModel.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipModel {
            if (swapper == null) {
                return ConditionalTooltipModel(property, onTrue.bake(null, resources), onFalse.bake(null, resources))
            }
            val slot = createCacheSlot(swapper, property, ConditionalItemModelProperty::type)

            return ConditionalTooltipModel(
                { stack, level, owner, seed, context -> (level?.let(slot::compute) ?: property).get(stack, level, owner, seed, context) },
                onTrue.bake(swapper, resources),
                onFalse.bake(swapper, resources),
            )
        }

        companion object {

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec {
                it.group(
                    ConditionalItemModelProperties.MAP_CODEC.forGetter(Unbaked::property),
                    TooltipModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue),
                    TooltipModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse),
                ).apply(it, ::Unbaked)
            }
        }
    }
}
