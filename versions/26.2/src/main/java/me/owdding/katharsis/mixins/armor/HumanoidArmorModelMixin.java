package me.owdding.katharsis.mixins.armor;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.katharsis.features.armor.models.ArmorModelState;
import me.owdding.katharsis.hooks.armor.LivingEntityRenderStateHook;
import me.owdding.katharsis.utils.geometry.BedrockGeometryRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidArmorLayer.class, priority = 2000)
public abstract class HumanoidArmorModelMixin<S extends HumanoidRenderState, A extends HumanoidModel<S>> {

    @Shadow
    protected abstract A getArmorModel(S renderState, EquipmentSlot slot);

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void katharsis$onBake(
        CallbackInfo ci,
        @Local(argsOnly = true) S state,
        @Local(argsOnly = true) EquipmentSlot slot,
        @Local(argsOnly = true) SubmitNodeCollector nodes,
        @Local(argsOnly = true) PoseStack stack,
        @Local(argsOnly = true) int light
    ) {
        if (!(state instanceof LivingEntityRenderStateHook hook)) return;
        if (!(hook.katharsis$getArmorDefinitionRenderState().fromSlot(slot) instanceof ArmorModelState.Bedrock renderer)) return;

        A model = this.getArmorModel(state, slot);

        var textures = renderer.getTextures();
        var colors = renderer.getColors();
        var isTranslucent = renderer.isTranslucent();

        for (int i = 0; i < renderer.getLayers(); i++) {
            var texture = textures[i];
            var color = colors[i];

            var renderType = isTranslucent ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutout(texture);

            nodes.order(i + 1).submitCustomGeometry(stack, renderType, (pose, consumer) -> {
                model.setupAnim(state);
                BedrockGeometryRenderer.render(renderer.getGeometry(), slot, model, pose, consumer, color, light, OverlayTexture.NO_OVERLAY);
            });
        }
        ci.cancel();
    }
}
