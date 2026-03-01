//~ named_identifier
package me.owdding.catharsis.hooks.items;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemStackHook {

    void catharsis$setExtraId(@NotNull Identifier id);
    @Nullable Identifier catharsis$getExtraId();

    void catharsis$setDisabled(boolean disabled);
    boolean catharsis$isDisabled();
}
