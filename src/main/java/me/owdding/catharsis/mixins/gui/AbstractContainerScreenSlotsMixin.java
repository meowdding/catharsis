package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.gui.modifications.GuiModifiers;
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier;
import me.owdding.catharsis.hooks.gui.SlotHook;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenSlotsMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow @Final protected T menu;

    protected AbstractContainerScreenSlotsMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void catharsis$onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        for (var slot : this.menu.slots) {
            var slotModifier = modifier != null ? modifier.getSlot(slot.index) : null;
            var hook = (SlotHook) slot;

            hook.catharsis$setPosition(Optionull.map(slotModifier, SlotModifier::getPosition));
            hook.catharsis$setHidden(slotModifier != null && slotModifier.getHidden());
            hook.catharsis$setHighlightable(slotModifier == null || slotModifier.getHighlightable());
        }
    }

    @ModifyReturnValue(method = "hasClickedOutside", at = @At("RETURN"))
    private boolean catharsis$modifyHasClickedOutside(boolean original, @Local(argsOnly = true, ordinal = 0) double mouseX, @Local(argsOnly = true, ordinal = 1) double mouseY) {
        if (!original) return false;
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier != null && modifier.getBounds() != null) {
            var clickableBounds = modifier.getBounds();
            var x = (this.width - clickableBounds.x) / 2;
            var y = (this.height - clickableBounds.y) / 2;
            return mouseX < x || mouseY < y || mouseX >= x + clickableBounds.x || mouseY >= y + clickableBounds.y;
        }
        return true;
    }
}
