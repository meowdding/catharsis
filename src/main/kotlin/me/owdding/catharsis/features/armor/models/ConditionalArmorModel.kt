//~ item_holder
package me.owdding.catharsis.features.armor.models

//? = 1.21.8
/*import me.owdding.catharsis.utils.extensions.asLivingEntity*/
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
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

class ConditionalArmorModel(
    private val property: ItemModelPropertyTest,
    private val onTrue: ArmorModel,
    private val onFalse: ArmorModel,
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        return if (property.get(stack, level, owner?.asLivingEntity(), seed, ItemDisplayContext.NONE)) {
            onTrue.resolve(stack, level, owner, seed)
        } else {
            onFalse.resolve(stack, level, owner, seed)
        }
    }

    class Unbaked(
        val property: ConditionalItemModelProperty,
        val onTrue: ArmorModel.Unbaked,
        val onFalse: ArmorModel.Unbaked,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            if (swapper == null) {
                return ConditionalArmorModel(property, onTrue.bake(null, resources), onFalse.bake(null, resources))
            }
            val slot = createCacheSlot(swapper, property, ConditionalItemModelProperty::type)

            return ConditionalArmorModel(
                {  stack, level, owner, seed, context -> (level?.let(slot::compute) ?: property).get(stack, level, owner, seed, context) },
                onTrue.bake(swapper, resources),
                onFalse.bake(swapper, resources),
            )
        }

        companion object {

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { it.group(
                ConditionalItemModelProperties.MAP_CODEC.forGetter(Unbaked::property),
                ArmorModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue),
                ArmorModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse),
            ).apply(it, ::Unbaked) }
        }
    }
}
