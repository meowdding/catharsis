package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.tooltip.TooltipFeature;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.helpers.McLevel;
import tech.thatgravyboat.skyblockapi.helpers.McPlayer;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @WrapOperation(
        method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
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
