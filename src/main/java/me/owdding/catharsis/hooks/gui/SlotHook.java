package me.owdding.catharsis.hooks.gui;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;

public interface SlotHook {

    void catharsis$setPosition(@Nullable Vector2ic position);

    void catharsis$setHidden(boolean hidden);

    void catharsis$setHighlightable(boolean highlightable);
}
