package me.owdding.catharsis.mixins.text;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.text.targets.ItemTextReplacements;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapOperation(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/TooltipProvider;addToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;Lnet/minecraft/core/component/DataComponentGetter;)V"))
    private void addToTooltip(
        TooltipProvider instance,
        Item.TooltipContext context,
        Consumer<Component> adder,
        TooltipFlag flag, DataComponentGetter components,
        Operation<Void> original
    ) {
        if (instance instanceof ItemLore hook) {
            hook.catharsis$addToTooltip((ItemStack)(Object)this, context, adder, flag, components);
        } else {
            original.call(instance, context, adder, flag, components);
        }
    }

    @ModifyReturnValue(method = "getHoverName", at = @At("RETURN"))
    private Component catharsis$modifyItemName(Component original) {
        return ItemTextReplacements.INSTANCE.replace((ItemStack) (Object) this, original);
    }
}
