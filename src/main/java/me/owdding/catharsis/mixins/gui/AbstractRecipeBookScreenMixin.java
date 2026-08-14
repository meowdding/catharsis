package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.gui.modifications.GuiModifiers;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractRecipeBookScreen.class)
public class AbstractRecipeBookScreenMixin {

    @Shadow
    @Final
    private RecipeBookComponent<?> recipeBookComponent;

    @WrapOperation(method = "initButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public GuiEventListener initButtons(AbstractRecipeBookScreen<?> instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        var activeModifier = GuiModifiers.getActiveModifier();
        if (activeModifier != null && activeModifier.getHideRecipeBookButton()) {
            if (this.recipeBookComponent.isVisible() && guiEventListener instanceof AbstractButton button) {
                button.onPress(new MouseButtonInfo(1, 0));
            }
            return guiEventListener;
        }
        return original.call(instance, guiEventListener);
    }

}
