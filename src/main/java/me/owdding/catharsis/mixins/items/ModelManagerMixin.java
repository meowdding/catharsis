//~ named_identifier
package me.owdding.catharsis.mixins.items;

import me.owdding.catharsis.hooks.items.ModelManagerHook;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin implements ModelManagerHook {

    @Shadow
    private Map<Identifier, ItemModel> bakedItemStackModels;

    @Override
    public boolean catharsis$hasCustomModel(Identifier model) {
        return this.bakedItemStackModels.containsKey(model);
    }
}
