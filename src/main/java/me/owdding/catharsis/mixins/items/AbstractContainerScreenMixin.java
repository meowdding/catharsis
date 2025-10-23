package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.hooks.items.AbstractContainerScreenHook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @WrapOperation(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;III)V"))
    private void catharsis$renderItem(GuiGraphics instance, ItemStack stack, int x, int y, int seed, Operation<Void> original, @Local(argsOnly = true) Slot slot) {
        AbstractContainerScreenHook.SLOT.set(slot);
        AbstractContainerScreenHook.HOVERED.set(this.hoveredSlot == slot);

        original.call(instance, stack, x, y, seed);

        AbstractContainerScreenHook.SLOT.remove();
        AbstractContainerScreenHook.HOVERED.remove();
    }
}
