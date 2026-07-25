package me.owdding.katharsis.hooks.entity;

import me.owdding.katharsis.features.entity.models.CustomEntityModel;

public interface EntityHook {

    default void katharsis$resetCustomModel() {
        throw new UnsupportedOperationException();
    }

    default CustomEntityModel katharsis$getCustomEntityModel() {
        throw new UnsupportedOperationException();
    }
}
