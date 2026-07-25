package me.owdding.katharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.owdding.katharsis.features.gui.definitions.GuiDefinitions;
import me.owdding.katharsis.features.gui.modifications.GuiModifiers;
import me.owdding.katharsis.features.gui.modifications.elements.GuiElementRenderLayer;
import me.owdding.katharsis.hooks.gui.AbstractContainerScreenHook;
import me.owdding.katharsis.hooks.gui.KatharsisScreenBounds;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
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
public abstract class AbstractContainerScreenElementsMixin<T extends AbstractContainerMenu> extends Screen implements AbstractContainerScreenHook {

    @Shadow @Final protected T menu;
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected int imageHeight;

    @Unique private KatharsisScreenBounds katharsis$bounds;

    protected AbstractContainerScreenElementsMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void katharsis$onInitHead(CallbackInfo ci) {
        GuiDefinitions.INSTANCE.forceUpdateInstantly((AbstractContainerScreen<T>) (Object) this);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void katharsis$onInitTail(CallbackInfo ci) {
        this.katharsis$bounds = new KatharsisScreenBounds(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    public KatharsisScreenBounds katharsis$containerBounds() {
        return katharsis$bounds;
    }

    @WrapWithCondition(
        method = "extractContents",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
    )
    private boolean katharsis$shouldRenderLabels(AbstractContainerScreen<?> instance, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        var modifier = GuiModifiers.getActiveModifier();
        return modifier == null || !modifier.getOverrideLabels();
    }

    @Inject(
        method = "extractContents",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", shift = At.Shift.AFTER)
    )
    private void katharsis$renderForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.katharsis$bounds == null) return;
        modifier.renderElements(
            GuiElementRenderLayer.FOREGROUND,
            guiGraphics,
            mouseX, mouseY,
            partialTick,
            this.katharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }


    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void katharsis$onMouseClicked(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.katharsis$bounds == null) return;
        modifier.handleInteraction(
            event.x(), event.y(),
            event.button(), true,
            this.katharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void katharsis$onMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.katharsis$bounds == null) return;
        modifier.handleInteraction(
            event.x(), event.y(),
            event.button(), false,
            this.katharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );
    }

    @Inject(
        method = "extractTooltip",
        at = @At("HEAD"),
        cancellable = true
    )
    private void katharsis$extractWidgetTooltips(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.katharsis$bounds == null) return;

        boolean renderedTooltip = modifier.renderTooltips(
            guiGraphics, mouseX, mouseY,
            this.katharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );

        if (renderedTooltip) {
            ci.cancel();
        }
    }
}
