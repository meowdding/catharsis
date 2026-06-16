package me.owdding.catharsis.hooks.entity;

import me.owdding.catharsis.features.entity.models.CustomEntityModel;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateHook {

    default void catharsis$setCustomEntityModelVariant(@Nullable CustomEntityModel.Variant variant) {
        throw new UnsupportedOperationException();
    }

    default @Nullable CustomEntityModel.Variant catharsis$getCustomEntityModelVariant() {
        throw new UnsupportedOperationException();
    }
}
