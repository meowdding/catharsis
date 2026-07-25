package me.owdding.katharsis.mixins.gui;

import me.owdding.katharsis.features.gui.modifications.GuiModifiers;
import me.owdding.katharsis.features.gui.modifications.elements.GuiElementRenderLayer;
import me.owdding.katharsis.hooks.gui.AbstractContainerScreenHook;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ScreenMixin implements AbstractContainerScreenHook {
    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Final
    @Shadow
    protected int imageWidth;
    @Final
    @Shadow
    protected int imageHeight;

    protected void katharsis$modify(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        var hasModifier = modifier != null && this.katharsis$containerBounds() != null;
        if (hasModifier && modifier.getOverrideBackground()) {
            ci.cancel();
        }

        if (hasModifier) {
            modifier.renderElements(
                GuiElementRenderLayer.BACKGROUND,
                graphics,
                mouseX, mouseY,
                delta,
                this.katharsis$containerBounds().updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
            );
        }
    }


    @Mixin(ContainerScreen.class)
    private abstract static class ContainerScreenMixin extends ScreenMixin {
        @Inject(
            method = "extractBackground",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
                shift = At.Shift.AFTER
            ),
            cancellable = true
        )
        private void elements(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
            katharsis$modify(graphics, mouseX, mouseY, delta, ci);
        }
    }

    @Mixin(InventoryScreen.class)
    private abstract static class InventoryScreenMixin  extends ScreenMixin {
        @Inject(
            method = "extractBackground",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
                shift = At.Shift.AFTER
            ),
            cancellable = true
        )
        private void elements(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
            katharsis$modify(graphics, mouseX, mouseY, delta, ci);
        }
    }
}
