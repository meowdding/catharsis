package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions;
import me.owdding.catharsis.hooks.items.AbstractContainerScreenHook;
import me.owdding.catharsis.hooks.items.ModelManagerHook;
import me.owdding.catharsis.utils.ItemUtils;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {

    @Unique
    private ModelManagerHook manager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void catharsis$storeModelManager(ModelManager modelManager, CallbackInfo ci) {
        this.manager = modelManager instanceof ModelManagerHook hook ? hook : null;
    }

    @ModifyExpressionValue(method = "appendItemLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object catharsis$modifyDataComponentType(Object original, @Local(argsOnly = true) ItemStack stack) {
        if (manager == null) return original;

        var guiId = GuiDefinitions.getSlot(AbstractContainerScreenHook.SLOT.get());
        var itemId = ItemUtils.INSTANCE.getCustomLocation(stack);
        var model = guiId != null ? guiId : itemId;

        return model == null || !manager.catharsis$hasCustomModel(model) ? original : model;
    }
}
