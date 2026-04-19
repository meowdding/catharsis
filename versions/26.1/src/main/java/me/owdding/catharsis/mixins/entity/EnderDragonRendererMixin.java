package me.owdding.catharsis.mixins.entity;

import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.entity.models.CustomEntityModel;
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
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            ordinal = 2
        ),
        method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"
    )
    public void catharsis$handleDragonTextureReplacement(Args args, @Local(argsOnly = true) EnderDragonRenderState state) {
        CustomEntityModel customEntityModel = state.catharsis$getCustomEntityModel();

        if (customEntityModel == null) return;

        args.set(
            3,
            RenderTypes.entityCutoutNoCull(customEntityModel.getTexture())
        );
    }

    @ModifyArgs(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            ordinal = 1
        ),
        method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"
    )
    public void catharsis$handleDragonTextureReplacementDecal(Args args, @Local(argsOnly = true) EnderDragonRenderState state) {
        CustomEntityModel customEntityModel = state.catharsis$getCustomEntityModel();

        if (customEntityModel == null) return;

        args.set(
            3,
            RenderTypes.entityDecal(customEntityModel.getTexture())
        );
    }
}
