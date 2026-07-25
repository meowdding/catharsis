package me.owdding.katharsis.hooks.gui;

import net.minecraft.world.inventory.MenuType;

public interface AbstractContainerMenuHook {

    default MenuType<?> katharsis$getMenuTypeOrNull() {
        return null;
    }
}
