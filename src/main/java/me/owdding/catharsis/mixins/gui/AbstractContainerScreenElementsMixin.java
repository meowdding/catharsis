package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.gui.modifications.GuiModifiers;
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer;
import me.owdding.catharsis.hooks.gui.CatharsisScreenBounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? >= 1.21.9
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenElementsMixin<T extends AbstractContainerMenu> {

    @Shadow @Final protected T menu;
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected int imageHeight;

    @Unique private CatharsisScreenBounds catharsis$bounds;

    @Inject(method = "init", at = @At("TAIL"))
    private void catharsis$onInit(CallbackInfo ci) {
        this.catharsis$bounds = new CatharsisScreenBounds(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @WrapWithCondition(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private boolean catharsis$shouldRenderLabels(AbstractContainerScreen<?> instance, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var modifier = GuiModifiers.getActiveModifier();
        return modifier == null || !modifier.getOverrideLabels();
    }

    @WrapOperation(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
    private void catharsis$renderBackground(AbstractContainerScreen<?> instance, GuiGraphics graphics, float partialTick, int mouseX, int mouseY, Operation<Void> original) {
        var modifier = GuiModifiers.getActiveModifier();
        var hasModifier = modifier != null && this.catharsis$bounds != null;
        if (!hasModifier || !modifier.getOverrideBackground()) {
            original.call(instance, graphics, partialTick, mouseX, mouseY);
        }

        if (hasModifier) {
            modifier.renderElements(
                GuiElementRenderLayer.BACKGROUND,
                graphics,
                mouseX, mouseY,
                partialTick,
                this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
            );
        }
    }

    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    private void catharsis$renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.catharsis$bounds == null) return;
        modifier.renderElements(
            GuiElementRenderLayer.FOREGROUND,
            guiGraphics,
            mouseX, mouseY,
            partialTick,
            this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }


    //? if >= 1.21.9 {
    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void catharsis$onMouseClicked(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.catharsis$bounds == null) return;
        modifier.handleInteraction(
            event.x(), event.y(),
            event.button(), true,
            this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void catharsis$onMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.catharsis$bounds == null) return;
        modifier.handleInteraction(
            event.x(), event.y(),
            event.button(), false,
            this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }
    //?} else {
    /*@Inject(method = "mouseClicked", at = @At("HEAD"))
    private void catharsis$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.catharsis$bounds == null) return;
        modifier.handleInteraction(
            mouseX, mouseY,
            button, true,
            this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void catharsis$onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.catharsis$bounds == null) return;
        modifier.handleInteraction(
            mouseX, mouseY,
            button, false,
            this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }
    *///?}
}
