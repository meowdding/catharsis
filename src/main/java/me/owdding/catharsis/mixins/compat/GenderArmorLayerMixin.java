package me.owdding.catharsis.mixins.compat;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.client.render.GenderArmorLayer;
import com.wildfire.client.render.GenderLayer;
import me.owdding.catharsis.features.armor.models.ArmorModelState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(value = GenderArmorLayer.class, remap = false)
public class GenderArmorLayerMixin<STATE extends HumanoidRenderState, MODEL extends HumanoidModel<STATE>> extends GenderLayer<STATE, MODEL> {

    public GenderArmorLayerMixin(RenderLayerParent<STATE, MODEL> render) {
        super(render);
    }

    @WrapMethod(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
        require = 0
    )
    private void catharsis$skipBedrock(PoseStack matrixStack, SubmitNodeCollector nodeCollector, int light, STATE state, float limbAngle, float limbDistance, Operation<Void> original) {
        // Bedrock Geo Models cant be supported
        if (!(state.catharsis$getArmorDefinitionRenderState().getChest() instanceof ArmorModelState.Bedrock)) {
            original.call(matrixStack, nodeCollector, light, state, limbAngle, limbDistance);
        }
    }
}
