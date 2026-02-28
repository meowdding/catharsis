package me.owdding.catharsis.hooks.entity;

import me.owdding.catharsis.features.entity.models.CustomEntityModel;

public interface EntityHook {

    default void catharsis$resetCustomModel() {
        throw new UnsupportedOperationException();
    }

    default CustomEntityModel catharsis$getCustomEntityModel() {
        throw new UnsupportedOperationException();
    }
}
