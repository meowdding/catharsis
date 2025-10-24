package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.owdding.catharsis.hooks.gui.SlotHook;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Slot.class)
public class SlotMixin implements SlotHook {

    @Shadow
    @Final
    @Mutable
    public int x;

    @Shadow
    @Final
    @Mutable
    public int y;

    @Unique
    private @Nullable Vector2ic catharsis$originalPosition = null;

    @Unique
    private boolean catharsis$hidden = false;

    @Unique
    private boolean catharsis$highlightable = true;

    @ModifyReturnValue(method = "isActive", at = @At("RETURN"))
    private boolean catharsis$modifyIsActive(boolean original) {
        return original && !this.catharsis$hidden;
    }

    @ModifyReturnValue(method = "isHighlightable", at = @At("RETURN"))
    private boolean catharsis$modifyIsHighlightable(boolean original) {
        return this.catharsis$highlightable && original;
    }

    // Hook Overrides
    @Override
    public void catharsis$setPosition(@Nullable Vector2ic position) {
        if (this.catharsis$originalPosition == null && position != null) {
            this.catharsis$originalPosition = new Vector2i(this.x, this.y);
        } else if (this.catharsis$originalPosition != null && position == null) {
            this.x = this.catharsis$originalPosition.x();
            this.y = this.catharsis$originalPosition.y();
        } else if (position != null) {
            this.x = position.x();
            this.y = position.y();
        }
    }

    @Override
    public void catharsis$setHidden(boolean hidden) {
        this.catharsis$hidden = hidden;
    }

    @Override
    public void catharsis$setHighlightable(boolean highlightable) {
        this.catharsis$highlightable = highlightable;
    }
}
