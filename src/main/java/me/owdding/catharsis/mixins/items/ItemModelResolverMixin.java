package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions;
import me.owdding.catharsis.hooks.items.AbstractContainerScreenHook;
import me.owdding.catharsis.utils.ItemUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.helpers.McPlayer;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {

    @Unique
    private ModelManager manager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void catharsis$storeModelManager(ModelManager modelManager, CallbackInfo ci) {
        this.manager = modelManager;
    }

    @ModifyExpressionValue(method = "appendItemLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object catharsis$modifyDataComponentType(
        Object original,
        @Local(argsOnly = true) ItemStack stack,
        @Local(argsOnly = true) ItemStackRenderState state
    ) {
        if (manager == null || state == null) return original;
        if (!state.catharsis$canFallthrough()) return original;

        var isCarried = McPlayer.INSTANCE.getSelf() instanceof LocalPlayer player && player.containerMenu.getCarried() == stack;
        var slot = AbstractContainerScreenHook.SLOT.get();
        var guiId = isCarried ? GuiDefinitions.getSlot(stack) : (slot != null ? GuiDefinitions.getSlot(slot.index) : null);
        var itemId = ItemUtils.INSTANCE.getCustomLocation(stack);
        var model = guiId != null ? guiId : itemId;

        return model == null || !manager.catharsis$hasCustomModel(model) ? original : model;
    }
}
