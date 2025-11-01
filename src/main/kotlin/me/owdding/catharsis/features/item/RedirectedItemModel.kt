package me.owdding.catharsis.features.item

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

data class RedirectedItemModel(
    private val slot: EquipmentSlot,
    private val model: ItemModel
) : ItemModel {

    override fun update(state: ItemStackRenderState, stack: ItemStack, resolver: ItemModelResolver, context: ItemDisplayContext, level: ClientLevel?, owner: ItemOwner?, seed: Int) {
        state.appendModelIdentityElement(this)
        this.model.update(
            state,
            owner?.asLivingEntity()?.getItemBySlot(this.slot) ?: ItemStack.EMPTY,
            resolver,
            context,
            level,
            owner,
            seed
        )
    }

    data class Unbaked(
        private val slot: EquipmentSlot,
        private val model: ItemModel.Unbaked
    ) : ItemModel.Unbaked {

        override fun type(): MapCodec<out ItemModel.Unbaked> = CODEC
        override fun bake(context: ItemModel.BakingContext): ItemModel = RedirectedItemModel(this.slot, this.model.bake(context))
        override fun resolveDependencies(resolver: ResolvableModel.Resolver) = this.model.resolveDependencies(resolver)

        companion object {

            val ID = Catharsis.id("redirect")
            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { it.group(
                EquipmentSlot.CODEC.fieldOf("slot").forGetter(Unbaked::slot),
                ItemModels.CODEC.fieldOf("model").forGetter(Unbaked::model)
            ).apply(it, ::Unbaked) }
        }
    }
}
