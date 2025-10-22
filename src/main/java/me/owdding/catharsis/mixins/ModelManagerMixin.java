package me.owdding.catharsis.mixins;

import me.owdding.catharsis.hooks.ModelManagerHook;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin implements ModelManagerHook {

    @Shadow
    private Map<ResourceLocation, ItemModel> bakedItemStackModels;

    @Override
    public boolean catharsis$hasCustomModel(ResourceLocation model) {
        return this.bakedItemStackModels.containsKey(model);
    }
}
