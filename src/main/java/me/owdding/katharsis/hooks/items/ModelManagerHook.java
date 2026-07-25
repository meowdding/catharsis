package me.owdding.katharsis.hooks.items;

import net.minecraft.resources.Identifier;

public interface ModelManagerHook {

    default boolean katharsis$hasCustomModel(Identifier model) {
        throw new UnsupportedOperationException();
    }
}
