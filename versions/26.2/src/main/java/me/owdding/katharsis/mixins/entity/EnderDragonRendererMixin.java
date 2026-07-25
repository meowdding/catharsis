package me.owdding.katharsis.mixins.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnderDragonRenderer.class)
public class EnderDragonRendererMixin {

    @ModifyArgs(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
        )
    )
    public void katharsis$handleDragonTextureReplacement(Args args, @Local(argsOnly = true) EnderDragonRenderState state) {
        var customVariant = state.katharsis$getCustomEntityModelVariant();

        if (customVariant == null) return;

        args.set(3, customVariant.getTexture());
    }

    @ModifyArgs(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
        )
    )
    public void katharsis$handleDragonTextureReplacementDecal(Args args, @Local(argsOnly = true) EnderDragonRenderState state) {
        var customVariant = state.katharsis$getCustomEntityModelVariant();

        if (customVariant == null) return;

        args.set(3, RenderTypes.eyes(customVariant.getTexture()));
    }
}
