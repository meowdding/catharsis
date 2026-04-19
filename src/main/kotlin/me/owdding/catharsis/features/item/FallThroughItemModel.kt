package me.owdding.catharsis.features.item

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.hooks.items.ItemStackRenderStateHook
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix4fc

object FallThroughItemModel: ItemModel {

    override fun update(state: ItemStackRenderState, stack: ItemStack, resolver: ItemModelResolver, context: ItemDisplayContext, level: ClientLevel?, owner: ItemOwner?, seed: Int) {
        state.appendModelIdentityElement(this)
        val hook = state as? ItemStackRenderStateHook ?: return
        if (!hook.`catharsis$canFallthrough`()) return
        hook.`catharsis$setCanFallthrough`(false)
        resolver.appendItemLayers(state, stack, context, level, owner, seed)
    }

    object Unbaked: ItemModel.Unbaked {

        val ID = Catharsis.id("fallthrough")
        val CODEC: MapCodec<Unbaked> = MapCodec.unit(Unbaked)

        override fun type(): MapCodec<out ItemModel.Unbaked> = CODEC
        override fun bake(context: ItemModel.BakingContext/*? if >= 26.1 >> ')'*/, transformation: Matrix4fc) = FallThroughItemModel
        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {}
    }
}
