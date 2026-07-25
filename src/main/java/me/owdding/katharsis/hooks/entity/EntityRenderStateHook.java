package me.owdding.katharsis.hooks.entity;

import me.owdding.katharsis.features.entity.models.CustomEntityModel;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateHook {

    default void katharsis$setCustomEntityModelVariant(@Nullable CustomEntityModel.Variant variant) {
        throw new UnsupportedOperationException();
    }

    default @Nullable CustomEntityModel.Variant katharsis$getCustomEntityModelVariant() {
        throw new UnsupportedOperationException();
    }
}
