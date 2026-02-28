package me.owdding.catharsis.hooks.entity;

import me.owdding.catharsis.features.entity.models.CustomEntityModel;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateHook {

    default void catharsis$setCustomEntityModel(@Nullable CustomEntityModel textureLocation) {
        throw new UnsupportedOperationException();
    }
    
    default @Nullable CustomEntityModel catharsis$getCustomEntityModel() {
        throw new UnsupportedOperationException();
    }
}
