package me.owdding.catharsis.hooks.gui;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;

public interface SlotHook {

    ThreadLocal<Boolean> INITIALIZING = new ThreadLocal<>();

    default void catharsis$setPosition(@Nullable Vector2ic position) {
        throw new UnsupportedOperationException();
    }

    default void catharsis$setHidden(boolean hidden) {
        throw new UnsupportedOperationException();
    }

    default void catharsis$setHighlightable(boolean highlightable) {
        throw new UnsupportedOperationException();
    }
}
