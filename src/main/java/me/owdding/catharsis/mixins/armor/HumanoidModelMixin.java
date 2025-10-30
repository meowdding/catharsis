package me.owdding.catharsis.mixins.armor;

import me.owdding.catharsis.features.armor.BodyPart;
import me.owdding.catharsis.features.armor.PartVisibilityState;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState> {

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At(value = "TAIL"))
    private void modifyModel(T renderState, CallbackInfo ci) {
        if (!(renderState instanceof LivingEntityRenderStateHook hook)) return;
        if (!hook.catharsis$getAndSetFirstDraw()) return;

        var self = (HumanoidModel<?>) (Object) this;
        var parts = hook.catharsis$getArmorDefinitionRenderState().getPartVisibility();

        self.head.visible = parts.getOrDefault(BodyPart.HEAD, PartVisibilityState.DEFAULT).getBase();
        self.hat.visible = self.hat.visible && parts.getOrDefault(BodyPart.HEAD, PartVisibilityState.DEFAULT).getOverlay();
        self.body.visible = self.body.visible && parts.getOrDefault(BodyPart.CHEST, PartVisibilityState.DEFAULT).getBase();
        self.rightArm.visible = self.rightArm.visible && parts.getOrDefault(BodyPart.RIGHT_ARM, PartVisibilityState.DEFAULT).getBase();
        self.leftArm.visible = self.leftArm.visible && parts.getOrDefault(BodyPart.LEFT_ARM, PartVisibilityState.DEFAULT).getBase();
        self.rightLeg.visible = self.rightLeg.visible && parts.getOrDefault(BodyPart.RIGHT_LEG, PartVisibilityState.DEFAULT).getBase();
        self.leftLeg.visible = self.leftLeg.visible && parts.getOrDefault(BodyPart.LEFT_LEG, PartVisibilityState.DEFAULT).getBase();

        if (self instanceof PlayerModel playerModel) {
            playerModel.jacket.visible = playerModel.jacket.visible && parts.getOrDefault(BodyPart.CHEST, PartVisibilityState.DEFAULT).getOverlay();
            playerModel.rightPants.visible = playerModel.rightPants.visible && parts.getOrDefault(BodyPart.RIGHT_LEG, PartVisibilityState.DEFAULT).getOverlay();
            playerModel.leftPants.visible = playerModel.leftPants.visible && parts.getOrDefault(BodyPart.LEFT_LEG, PartVisibilityState.DEFAULT).getOverlay();
            playerModel.rightSleeve.visible = playerModel.rightSleeve.visible && parts.getOrDefault(BodyPart.RIGHT_ARM, PartVisibilityState.DEFAULT).getOverlay();
            playerModel.leftSleeve.visible = playerModel.leftSleeve.visible && parts.getOrDefault(BodyPart.LEFT_ARM, PartVisibilityState.DEFAULT).getOverlay();
        }
    }

}
