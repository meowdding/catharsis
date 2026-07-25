package me.owdding.katharsis.hooks.items;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public interface ItemStackRenderStateHook {

    default void katharsis$setCanFallthrough(boolean canFallthrough) {
        throw new UnsupportedOperationException();
    }

    default boolean katharsis$canFallthrough() {
        throw new UnsupportedOperationException();
    }

    default int katharsis$layerCount() {
        throw new UnsupportedOperationException();
    }

    default ItemStackRenderState.LayerRenderState[] katharsis$getLayers(int from, int to) {
        throw new UnsupportedOperationException();
    }

}
