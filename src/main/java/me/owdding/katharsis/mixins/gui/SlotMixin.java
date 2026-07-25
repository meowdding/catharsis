package me.owdding.katharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.owdding.katharsis.events.SlotChangedEvent;
import me.owdding.katharsis.hooks.gui.SlotHook;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.helpers.McScreen;

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
    private @Nullable Vector2ic katharsis$originalPosition = null;

    @Unique
    private boolean katharsis$hidden = false;

    @Unique
    private boolean katharsis$highlightable = true;

    @ModifyReturnValue(method = "isActive", at = @At("RETURN"))
    private boolean katharsis$modifyIsActive(boolean original) {
        return original && !this.katharsis$hidden;
    }

    @ModifyReturnValue(method = "isHighlightable", at = @At("RETURN"))
    private boolean katharsis$modifyIsHighlightable(boolean original) {
        return this.katharsis$highlightable && original;
    }

    @Inject(method = "setChanged", at = @At("TAIL"))
    private void katharsis$onSetChanged(CallbackInfo ci) {
        if (SlotHook.INITIALIZING.get() == Boolean.TRUE) return;

        var self = (Slot) (Object) this;
        var menuScreen = McScreen.INSTANCE.getAsMenu();
        if (menuScreen == null) return;

        new SlotChangedEvent(self, menuScreen).post(SkyBlockAPI.getEventBus());
    }

    // Hook Overrides
    @Override
    public void katharsis$setPosition(@Nullable Vector2ic position) {
        if (this.katharsis$originalPosition == null && position != null) {
            this.katharsis$originalPosition = new Vector2i(this.x, this.y);
        } else if (this.katharsis$originalPosition != null && position == null) {
            this.x = this.katharsis$originalPosition.x();
            this.y = this.katharsis$originalPosition.y();
        } else if (position != null) {
            this.x = position.x();
            this.y = position.y();
        }
    }

    @Override
    public void katharsis$setHidden(boolean hidden) {
        this.katharsis$hidden = hidden;
    }

    @Override
    public void katharsis$setHighlightable(boolean highlightable) {
        this.katharsis$highlightable = highlightable;
    }
}
