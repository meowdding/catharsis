//~ named_identifier
package me.owdding.catharsis.mixins.items;

import me.owdding.catharsis.hooks.items.ItemStackHook;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackHook {

    @Unique
    private Identifier catharsis$id;

    @Unique
    private boolean catharsis$disabled;

    @Override
    public void catharsis$setExtraId(@NotNull Identifier id) {
        this.catharsis$id = id;
    }

    @Override
    public Identifier catharsis$getExtraId() {
        return this.catharsis$id;
    }

    @Override
    public void catharsis$setDisabled(boolean disabled) {
        this.catharsis$disabled = disabled;
    }

    @Override
    public boolean catharsis$isDisabled() {
        return this.catharsis$disabled;
    }
}
