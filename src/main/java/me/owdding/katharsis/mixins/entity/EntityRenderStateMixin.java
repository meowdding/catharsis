package me.owdding.katharsis.mixins.entity;

import me.owdding.katharsis.features.entity.models.CustomEntityModel;
import me.owdding.katharsis.hooks.entity.EntityRenderStateHook;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateHook {
    @Unique
    private CustomEntityModel.Variant katharsis$customModelVariant = null;

    @Override
    public void katharsis$setCustomEntityModelVariant(CustomEntityModel.Variant variant) {
        katharsis$customModelVariant = variant;
    }

    @Override
    public CustomEntityModel.Variant katharsis$getCustomEntityModelVariant() {
        return katharsis$customModelVariant;
    }
}
