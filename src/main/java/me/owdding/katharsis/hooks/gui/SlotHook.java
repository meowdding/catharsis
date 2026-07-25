package me.owdding.katharsis.hooks.gui;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;

public interface SlotHook {

    ThreadLocal<Boolean> INITIALIZING = new ThreadLocal<>();

    default void katharsis$setPosition(@Nullable Vector2ic position) {
        throw new UnsupportedOperationException();
    }

    default void katharsis$setHidden(boolean hidden) {
        throw new UnsupportedOperationException();
    }

    default void katharsis$setHighlightable(boolean highlightable) {
        throw new UnsupportedOperationException();
    }
}
