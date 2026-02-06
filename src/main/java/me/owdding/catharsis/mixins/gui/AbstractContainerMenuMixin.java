package me.owdding.catharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.owdding.catharsis.hooks.gui.SlotHook;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

    @WrapMethod(method = "initializeContents")
    private void catharsis$onInitializeContents(int stateId, List<ItemStack> items, ItemStack carried, Operation<Void> original) {
        SlotHook.INITIALIZING.set(true);
        original.call(stateId, items, carried);
        SlotHook.INITIALIZING.set(false);
    }
}
