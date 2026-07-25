package me.owdding.katharsis.mixins.items;

import me.owdding.katharsis.hooks.items.ItemStackHook;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackHook {

    @Unique
    private Identifier katharsis$id;

    @Unique
    private boolean katharsis$disabled;

    @Override
    public void katharsis$setExtraId(@NotNull Identifier id) {
        this.katharsis$id = id;
    }

    @Override
    public Identifier katharsis$getExtraId() {
        return this.katharsis$id;
    }

    @Override
    public void katharsis$setDisabled(boolean disabled) {
        this.katharsis$disabled = disabled;
    }

    @Override
    public boolean katharsis$isDisabled() {
        return this.katharsis$disabled;
    }
}
