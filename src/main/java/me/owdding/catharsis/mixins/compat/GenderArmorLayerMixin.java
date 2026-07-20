package me.owdding.catharsis.mixins.compat;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.catharsis.features.armor.models.ArmorModelState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderArmorLayer", remap = false)
public class GenderArmorLayerMixin {

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void catharsis$skipBedrock(PoseStack matrixStack, SubmitNodeCollector queue, int light, HumanoidRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
        if (state.catharsis$getArmorDefinitionRenderState().getChest() instanceof ArmorModelState.Bedrock) {
            // Bedrock Geo Models cant be supported
            ci.cancel();
        }
    }

    @WrapMethod(method = "renderBreastArmor")
    private void catharsis$swapTexture(
        Identifier texture,
        PoseStack poseStack,
        SubmitNodeCollector collector,
        HumanoidRenderState state,
        @Coerce Object side,
        int color,
        MutableBoolean glint,
        MutableInt order,
        Operation<Void> original
    ) {
        ArmorModelState chest = state.catharsis$getArmorDefinitionRenderState().getChest();
        int index = order.intValue() - 1;

        if (chest instanceof ArmorModelState.Texture custom && index < custom.getLayers()) {
            original.call(custom.getTextures()[index], poseStack, collector, state, side, custom.getColors()[index], glint, order);
        } else {
            original.call(texture, poseStack, collector, state, side, color, glint, order);
        }
    }
}
