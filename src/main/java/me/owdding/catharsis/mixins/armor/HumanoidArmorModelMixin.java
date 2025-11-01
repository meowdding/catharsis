package me.owdding.catharsis.mixins.armor;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.catharsis.features.armor.models.ArmorModelState;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import me.owdding.catharsis.utils.geometry.BedrockGeometryRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorModelMixin<S extends HumanoidRenderState, A extends HumanoidModel<S>> {

    @WrapWithCondition(
        method = "renderArmorPiece",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;II)V"
        )
    )
    private boolean catharsis$onBake(
        EquipmentLayerRenderer ignored1, EquipmentClientInfo.LayerType ignored2, ResourceKey<?> ignored3, Model<?> ignored4, Object ignored5,
        ItemStack ignored6, PoseStack ignored7, SubmitNodeCollector ignored8, int ignored9, int ignored10,
        @Local(argsOnly = true) S state,
        @Local(argsOnly = true) EquipmentSlot slot,
        @Local(argsOnly = true) SubmitNodeCollector nodes,
        @Local(argsOnly = true) PoseStack stack,
        @Local(argsOnly = true) int light,
        @Local A model
    ) {
        if (!(state instanceof LivingEntityRenderStateHook hook)) return true;
        if (!(hook.catharsis$getArmorDefinitionRenderState().fromSlot(slot) instanceof ArmorModelState.Bedrock renderer)) return true;

        var textures = renderer.getTextures();
        var colors = renderer.getColors();

        for (int i = 0; i < renderer.getLayers(); i++) {
            var texture = textures[i];
            var color = colors[i];

            nodes.order(i + 1).submitCustomGeometry(stack, RenderType.entityCutoutNoCull(texture), (pose, consumer) -> {
                model.setupAnim(state);
                BedrockGeometryRenderer.render(renderer.getGeometry(), slot, model, pose, consumer, color, light, OverlayTexture.NO_OVERLAY);
            });
        }
        return false;
    }
}
