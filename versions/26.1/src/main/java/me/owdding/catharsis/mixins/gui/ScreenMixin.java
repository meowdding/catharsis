package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.gui.modifications.GuiModifiers;
import me.owdding.catharsis.features.gui.modifications.elements.GuiElementRenderLayer;
import me.owdding.catharsis.hooks.gui.AbstractContainerScreenHook;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @WrapOperation(method = "extractRenderStateWithTooltipAndSubtitles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    public void catharsis$CancelThingy(Screen instance, GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, float f, Operation<Void> original) {
        original.call(instance, guiGraphicsExtractor, x, y, f);
    }


    @Mixin(AbstractContainerScreen.class)
    private abstract static class AbstractContainerScreenElementsMixin extends ScreenMixin implements AbstractContainerScreenHook {
        @Shadow protected int leftPos;
        @Shadow protected int topPos;
        @Shadow protected int imageWidth;
        @Shadow protected int imageHeight;

        @Override
        public void catharsis$CancelThingy(
            Screen instance,
            GuiGraphicsExtractor guiGraphicsExtractor,
            int x,
            int y,
            float f,
            Operation<Void> original
        ) {
            var modifier = GuiModifiers.getActiveModifier();
            var hasModifier = modifier != null && this.catharsis$containerBounds() != null;
            if (!hasModifier || !modifier.getOverrideBackground()) {
                original.call(instance, guiGraphicsExtractor, x, y, f);
            }

            if (hasModifier) {
                modifier.renderElements(
                    GuiElementRenderLayer.BACKGROUND,
                    guiGraphicsExtractor,
                    x, y,
                    f,
                    this.catharsis$containerBounds().updateOrGet(this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
                );
            }
        }
    }
}
