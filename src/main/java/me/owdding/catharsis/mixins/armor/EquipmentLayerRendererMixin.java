package me.owdding.catharsis.mixins.armor;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.catharsis.features.armor.models.ArmorModelState;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.Optionull;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Debug(export = true)
@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

    @Inject(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/DyedItemColor;getOrDefault(Lnet/minecraft/world/item/ItemStack;I)I")
    )
    private <S> void catharsis$modifyColorForLayer(
        CallbackInfo ci,
        @Local(argsOnly = true) S renderState,
        @Local(argsOnly = true) ItemStack stack,
        @Local(ordinal = 0) LocalRef<List<EquipmentClientInfo.Layer>> layers,
        @Share("texture") LocalRef<ArmorModelState.@Nullable Texture> stateRef
    ) {
        var texture = catharsis$getTextureForSlot(renderState, stack);
        stateRef.set(texture);

        if (texture != null) {
            layers.set(List.of());
        }
    }

    @Inject(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/core/component/DataComponents;TRIM:Lnet/minecraft/core/component/DataComponentType;")
    )
    private <S> void catharsis$modifyColorForLayer(
        CallbackInfo ci,

        @Local(argsOnly = true) Model<? super S> model,
        @Local(argsOnly = true) S renderState,
        @Local(argsOnly = true) PoseStack stack,
        @Local(argsOnly = true, ordinal = 0) int light,
        @Local(argsOnly = true, ordinal = 1) int outline,

        @Local(argsOnly = true) ItemStack item,
        @Local(argsOnly = true) SubmitNodeCollector nodes,
        @Local(ordinal = 4) LocalIntRef index,
        @Share("texture") LocalRef<ArmorModelState.@Nullable Texture> stateRef
    ) {
        var state = stateRef.get();
        if (state != null) {
            var applyGlint = item.hasFoil();

            var textures = state.getTextures();
            var colors = state.getColors();
            var startIndex = index.get();

            for (int i = 0; i < state.getLayers(); i++) {
                var texture = RenderType.armorCutoutNoCull(textures[i]);
                var color = ARGB.opaque(colors[i]);

                nodes
                    .order(startIndex++)
                    .submitModel(model, renderState, stack, texture, light, OverlayTexture.NO_OVERLAY, color, null, outline, null);

                if (applyGlint) {
                    nodes
                        .order(startIndex++)
                        .submitModel(model, renderState, stack, RenderType.armorEntityGlint(), light, OverlayTexture.NO_OVERLAY, color, null, outline, null);
                }

                applyGlint = false;
            }

            index.set(startIndex);
        }
    }

    @Unique
    private static ArmorModelState.Texture catharsis$getTextureForSlot(Object state, ItemStack stack) {
        if (state instanceof LivingEntityRenderStateHook hook) {
            var slot = Optionull.map(stack.get(DataComponents.EQUIPPABLE), Equippable::slot);
            if (slot == null) return null;

            var renderer = hook.catharsis$getArmorDefinitionRenderState().fromSlot(slot);
            return renderer instanceof ArmorModelState.Texture texture ? texture : null;
        }
        return null;
    }
}
