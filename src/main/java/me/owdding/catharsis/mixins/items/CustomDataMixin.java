package me.owdding.catharsis.mixins.items;

import me.owdding.catharsis.hooks.items.CustomDataHook;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CustomData.class)
public class CustomDataMixin implements CustomDataHook {

    @Shadow
    @Final
    private CompoundTag tag;

    @Override
    public @Nullable String catharsis$getString(String key) {
        return this.tag.getString(key).orElse(null);
    }

    @Override
    public @Nullable Boolean catharsis$getBoolean(String key) {
        return this.tag.getBoolean(key).orElse(null);
    }
}
