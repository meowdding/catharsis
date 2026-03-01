package me.owdding.catharsis.hooks.items;

import org.jetbrains.annotations.Nullable;

public interface CustomDataHook {

    @Nullable String catharsis$getString(String key);

    @Nullable Boolean catharsis$getBoolean(String key);
}
