package me.owdding.catharsis.mixins.entity;

import me.owdding.catharsis.features.entity.models.CustomEntityModel;
import me.owdding.catharsis.hooks.entity.EntityRenderStateHook;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateHook {
    @Unique
    private CustomEntityModel.Variant catharsis$customModelVariant = null;

    @Override
    public void catharsis$setCustomEntityModelVariant(CustomEntityModel.Variant variant) {
        catharsis$customModelVariant = variant;
    }

    @Override
    public CustomEntityModel.Variant catharsis$getCustomEntityModelVariant() {
        return catharsis$customModelVariant;
    }
}
