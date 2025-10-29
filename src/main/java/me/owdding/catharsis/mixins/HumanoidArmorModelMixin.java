package me.owdding.catharsis.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.catharsis.Catharsis;
import me.owdding.catharsis.utils.geometry.BedrockGeometryRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorModelMixin {

    @WrapOperation(
        method = "renderArmorPiece",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;II)V"
        )
    )
    private static void catharsis$onBake(
        EquipmentLayerRenderer instance,
        EquipmentClientInfo.LayerType layerType,
        ResourceKey<?> resourceKey,
        Model<?> model,
        Object state,
        ItemStack stack,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int light, int outlineColor,
        Operation<Void> original
    ) {
        submitNodeCollector.order(1).submitCustomGeometry(
            poseStack,
            RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/equipment/humanoid/iron_armor.png")),
            (pose, consumer) -> {

                BedrockGeometryRenderer.INSTANCE.render(Catharsis.getModel(), pose, consumer);
            }
        );
    }
}
