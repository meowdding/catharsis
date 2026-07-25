package me.owdding.katharsis.mixins.items;

import me.owdding.katharsis.hooks.items.ItemStackRenderStateHook;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public class ItemStackRenderStateMixin implements ItemStackRenderStateHook {

    @Shadow private int activeLayerCount;
    @Shadow private ItemStackRenderState.LayerRenderState[] layers;
    @Unique private boolean katharsis$canFallthrough = true;

    @Inject(method = "clear", at = @At("HEAD"))
    private void katharsis$resetCanFallthrough(CallbackInfo ci) {
        this.katharsis$canFallthrough = true;
    }

    @Override
    public void katharsis$setCanFallthrough(boolean canFallthrough) {
        this.katharsis$canFallthrough = canFallthrough;
    }

    @Override
    public boolean katharsis$canFallthrough() {
        return this.katharsis$canFallthrough;
    }

    @Override
    public int katharsis$layerCount() {
        return this.activeLayerCount;
    }

    @Override
    public ItemStackRenderState.LayerRenderState[] katharsis$getLayers(int fromInclusive, int toExclusive) {
        if (fromInclusive < 0 || toExclusive > this.activeLayerCount || fromInclusive >= toExclusive) {
            return null;
        }
        if (this.layers == null || this.layers.length < toExclusive) {
            return null;
        }

        var result = new ItemStackRenderState.LayerRenderState[toExclusive - fromInclusive];
        System.arraycopy(this.layers, fromInclusive, result, 0, result.length);
        return result;
    }
}
