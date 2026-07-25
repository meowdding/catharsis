package me.owdding.katharsis.mixins.entity;

import me.owdding.katharsis.features.entity.models.CustomEntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(
        method = "extractRenderState",
        at = @At("TAIL")
    )
    public void extractRenderState(T entity, S reusedState, float partialTick, CallbackInfo ci) {
        CustomEntityModel customModel = entity.katharsis$getCustomEntityModel();

        if (customModel != null) {
            reusedState.katharsis$setCustomEntityModelVariant(customModel.getVariant(entity));
        } else {
            reusedState.katharsis$setCustomEntityModelVariant(null);
        }
    }
}
