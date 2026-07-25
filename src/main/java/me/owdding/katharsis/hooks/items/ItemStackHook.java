package me.owdding.katharsis.hooks.items;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemStackHook {

    void katharsis$setExtraId(@NotNull Identifier id);
    @Nullable Identifier katharsis$getExtraId();

    void katharsis$setDisabled(boolean disabled);
    boolean katharsis$isDisabled();
}
