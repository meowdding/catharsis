package me.owdding.katharsis.mixins.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.owdding.katharsis.hooks.gui.AbstractContainerMenuHook;
import me.owdding.katharsis.hooks.gui.SlotHook;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements AbstractContainerMenuHook {

    @Shadow
    @Final
    @Nullable
    private MenuType<?> menuType;

    @WrapMethod(method = "initializeContents")
    private void katharsis$onInitializeContents(int stateId, List<ItemStack> items, ItemStack carried, Operation<Void> original) {
        SlotHook.INITIALIZING.set(true);
        original.call(stateId, items, carried);
        SlotHook.INITIALIZING.set(false);
    }

    @Override
    public MenuType<?> katharsis$getMenuTypeOrNull() {
        return this.menuType;
    }
}
