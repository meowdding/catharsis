package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions;
import me.owdding.catharsis.features.imc.ImcHandler;
import me.owdding.catharsis.features.pack.PackConflictManager;
import me.owdding.catharsis.hooks.items.AbstractContainerScreenHook;
import me.owdding.catharsis.utils.SkyBlockIdentifierResolver;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
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
        if (state == null) return original;
        if (!state.catharsis$canFallthrough()) return original;

        return catharsis$getCustomModel(original, stack);
    }

    @ModifyExpressionValue(
        method = {"shouldPlaySwapAnimation", "swapAnimationScale"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;")
    )
    private Object catharsis$modifyDataComponentType(Object original, @Local(argsOnly = true) ItemStack stack) {
        return catharsis$getCustomModel(original, stack);
    }

    @Unique
    private Object catharsis$getCustomModel(Object original, ItemStack stack) {
        if (manager == null) return original;
        if (ImcHandler.isDisabled(stack)) return original;

        if (original instanceof Identifier id && id.getNamespace().equals("hypixel_skyblock") && !PackConflictManager.getOverrideHypixel()) {
            return original;
        }

        var isCarried = McPlayer.INSTANCE.getSelf() instanceof LocalPlayer player && player.containerMenu.getCarried() == stack;
        var slot = AbstractContainerScreenHook.SLOT.get();

        var guiId = isCarried ? GuiDefinitions.getSlot(stack) : (slot != null ? GuiDefinitions.getSlot(slot.index) : null);
        if (guiId != null && manager.catharsis$hasCustomModel(guiId)) {
            return guiId;
        }

        var itemId = SkyBlockIdentifierResolver.resolveModelId(manager::catharsis$hasCustomModel, stack);
        if (itemId != null && manager.catharsis$hasCustomModel(itemId)) {
            return itemId;
        }

        return original;
    }
}
