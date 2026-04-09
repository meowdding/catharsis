package me.owdding.catharsis.hooks.gui;

import net.minecraft.world.inventory.MenuType;

public interface AbstractContainerMenuHook {

    default MenuType<?> catharsis$getMenuTypeOrNull() {
        return null;
    }
}
