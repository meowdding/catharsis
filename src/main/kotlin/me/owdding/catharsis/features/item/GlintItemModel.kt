package me.owdding.catharsis.features.item

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.hooks.items.ItemStackRenderStateHook
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemModels
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix4fc

data class GlintItemModel(
    val glint: Boolean,
    val model: ItemModel,
) : ItemModel {

    override fun update(
        state: ItemStackRenderState,
        stack: ItemStack,
        resolver: ItemModelResolver,
        context: ItemDisplayContext,
        level: ClientLevel?,
        owner: ItemOwner?,
        seed: Int,
    ) {
        val hook = state as? ItemStackRenderStateHook ?: return
        val start = hook.`catharsis$layerCount`()

        state.appendModelIdentityElement(this)
        model.update(state, stack, resolver, context, level, owner, seed)

        val end = hook.`catharsis$layerCount`()
        if (start < end) {
            val layers = hook.`catharsis$getLayers`(start, end) ?: return
            val foil = if (glint) ItemStackRenderState.FoilType.STANDARD else ItemStackRenderState.FoilType.NONE
            if (foil != ItemStackRenderState.FoilType.NONE) {
                state.setAnimated()
            }

            for (layer in layers) {
                layer.setFoilType(foil)
            }
        }
    }

    data class Unbaked(
        val glint: Boolean,
        val model: ItemModel.Unbaked,
    ) : ItemModel.Unbaked {

        override fun type(): MapCodec<out ItemModel.Unbaked> = CODEC
        override fun bake(context: ItemModel.BakingContext/*? if >= 26.1 >> ')'*/, transformation: Matrix4fc): ItemModel =
            GlintItemModel(this.glint, this.model.bake(context/*? if >= 26.1 >> ')'*/, transformation))

        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
            model.resolveDependencies(resolver)
        }

        companion object {
            val ID = Catharsis.id("glint")
            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec {
                it.group(
                    Codec.BOOL.fieldOf("glint").forGetter(Unbaked::glint),
                    ItemModels.CODEC.fieldOf("model").forGetter(Unbaked::model),
                ).apply(it, ::Unbaked)
            }
        }
    }
}
