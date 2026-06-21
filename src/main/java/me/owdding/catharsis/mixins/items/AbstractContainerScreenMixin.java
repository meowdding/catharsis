package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.tooltip.TooltipFeature;
import me.owdding.catharsis.hooks.items.AbstractContainerScreenHook;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.helpers.McLevel;
import tech.thatgravyboat.skyblockapi.helpers.McPlayer;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @WrapOperation(
        method = "extractSlot",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/item/ItemStack;III)V")
    )
    private void catharsis$renderItem(GuiGraphicsExtractor instance, ItemStack stack, int x, int y, int seed, Operation<Void> original, @Local(argsOnly = true) Slot slot) {
        AbstractContainerScreenHook.SLOT.set(slot);
        AbstractContainerScreenHook.HOVERED.set(this.hoveredSlot == slot);

        original.call(instance, stack, x, y, seed);

        AbstractContainerScreenHook.SLOT.remove();
        AbstractContainerScreenHook.HOVERED.remove();
    }

    @WrapOperation(
        method = "extractTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
        )
    )
    private <T> T catharsis$wrapGetTooltipStyle(ItemStack instance, DataComponentType<@NotNull T> dataComponentType, Operation<T> original) {
        var definition = TooltipFeature.getDefinition();
        if (definition != null) {
            var state = definition.resolve(instance, McLevel.INSTANCE.getSelfOrNull(), McPlayer.INSTANCE.getSelf());
            if (state != null) {
                //noinspection unchecked
                return (T) state;
            }
        }

        return original.call(instance, dataComponentType);
    }
}
