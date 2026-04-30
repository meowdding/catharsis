package me.owdding.catharsis.hooks.items;

import net.minecraft.resources.Identifier;

public interface ModelManagerHook {

    default boolean catharsis$hasCustomModel(Identifier model) {
        throw new UnsupportedOperationException();
    }
}
