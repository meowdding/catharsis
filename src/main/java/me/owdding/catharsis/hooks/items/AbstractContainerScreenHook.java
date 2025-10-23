package me.owdding.catharsis.hooks.items;

import net.minecraft.world.inventory.Slot;

public interface AbstractContainerScreenHook {

    ThreadLocal<Slot> SLOT = new ThreadLocal<>();
    ThreadLocal<Boolean> HOVERED = new ThreadLocal<>();
}
