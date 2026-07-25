package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions;
import me.owdding.catharsis.features.gui.modifications.GuiModifiers;
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer;
import me.owdding.catharsis.hooks.gui.AbstractContainerScreenHook;
import me.owdding.catharsis.hooks.gui.CatharsisScreenBounds;
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

    @Unique private CatharsisScreenBounds catharsis$bounds;

    protected AbstractContainerScreenElementsMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void catharsis$onInitHead(CallbackInfo ci) {
        GuiDefinitions.INSTANCE.forceUpdateInstantly((AbstractContainerScreen<T>) (Object) this);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void catharsis$onInitTail(CallbackInfo ci) {
        this.catharsis$bounds = new CatharsisScreenBounds(this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    public CatharsisScreenBounds catharsis$containerBounds() {
        return catharsis$bounds;
    }

    @WrapWithCondition(
        method = "extractContents",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
    )
    private boolean catharsis$shouldRenderLabels(AbstractContainerScreen<?> instance, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        var modifier = GuiModifiers.getActiveModifier();
        return modifier == null || !modifier.getOverrideLabels();
    }

    @Inject(
        method = "extractContents",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", shift = At.Shift.AFTER)
    )
    private void catharsis$renderForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
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

    @Inject(
        method = "extractTooltip",
        at = @At("HEAD"),
        cancellable = true
    )
    private void catharsis$extractWidgetTooltips(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier == null || this.catharsis$bounds == null) return;

        boolean renderedTooltip = modifier.renderTooltips(
            guiGraphics, mouseX, mouseY,
            this.catharsis$bounds.updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        );

        if (renderedTooltip) {
            ci.cancel();
        }
    }
}
