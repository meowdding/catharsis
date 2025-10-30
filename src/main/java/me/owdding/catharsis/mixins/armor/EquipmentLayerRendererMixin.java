package me.owdding.catharsis.mixins.armor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.armor.models.ArmorModelState;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.Optionull;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

    @ModifyExpressionValue(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;getColorForLayer(Lnet/minecraft/client/resources/model/EquipmentClientInfo$Layer;I)I")
    )
    private <S> int catharsis$modifyColorForLayer(int original, @Local(argsOnly = true) S renderState, @Local(argsOnly = true) ItemStack stack) {
        if (original != -1 &&  catharsis$getTextureForSlot(renderState, stack) != null) {
            return 0;
        }
        return original;
    }

    @ModifyVariable(
        method = {
            "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V"
        },
        at = @At("STORE"), ordinal = 1
    )
    private <S> ResourceLocation catharsis$modifyTextureLocation(ResourceLocation original, @Local(argsOnly = true) S renderState, @Local(argsOnly = true) ItemStack stack) {
        var modified = catharsis$getTextureForSlot(renderState, stack);
        if (modified != null) {
            return modified;
        }
        return original;
    }

    @Unique
    private static ResourceLocation catharsis$getTextureForSlot(Object state, ItemStack stack) {
        if (state instanceof LivingEntityRenderStateHook hook) {
            var slot = Optionull.map(stack.get(DataComponents.EQUIPPABLE), Equippable::slot);
            if (slot == null) return null;

            var renderer = hook.catharsis$getArmorDefinitionRenderState().fromSlot(slot);
            return renderer instanceof ArmorModelState.Texture texture ? texture.getTexture() : null;
        }
        return null;
    }
}
