package me.owdding.katharsis.hooks.items;

import org.jetbrains.annotations.Nullable;

public interface CustomDataHook {

    @Nullable String katharsis$getString(String key);

    @Nullable Boolean katharsis$getBoolean(String key);
}
