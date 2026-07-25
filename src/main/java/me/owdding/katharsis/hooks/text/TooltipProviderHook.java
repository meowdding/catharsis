package me.owdding.katharsis.hooks.text;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

public interface TooltipProviderHook {

    default void katharsis$addToTooltip(ItemStack stack, Item.TooltipContext context, Consumer<Component> adder, TooltipFlag flag, DataComponentGetter components) {
        throw new UnsupportedOperationException();
    }
}
