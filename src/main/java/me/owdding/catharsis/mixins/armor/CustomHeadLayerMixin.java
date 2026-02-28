package me.owdding.catharsis.mixins.armor;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.catharsis.features.armor.models.ArmorModelState;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CustomHeadLayer.class)
public class CustomHeadLayerMixin {

    @WrapMethod(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V")
    private void catharsis$modifyRenderTypeForCustomHeadLayer(
        PoseStack stack, SubmitNodeCollector collector, int packedLight, EntityRenderState state, float yRot, float xRot, Operation<Void> original
    ) {
        if (!(state instanceof LivingEntityRenderStateHook hook) || !(hook.catharsis$getArmorDefinitionRenderState().getHead() instanceof ArmorModelState.Bedrock)) {
            original.call(stack, collector, packedLight, state, yRot, xRot);
        }
    }
}
