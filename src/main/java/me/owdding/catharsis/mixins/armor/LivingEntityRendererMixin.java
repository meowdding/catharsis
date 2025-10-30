package me.owdding.catharsis.mixins.armor;

import me.owdding.catharsis.features.armor.BodyPart;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public class LivingEntityRendererMixin {

    @Unique
    private Object self() {
        return this;
    }

    @Inject(method = "setupAnim", at = @At(value = "RETURN"))
    private <S> void modifyModel(S renderState, CallbackInfo ci) {
        if (!(renderState instanceof LivingEntityRenderStateHook hook)) {
            return;
        }
        var armorState = hook.catharsis$getArmorDefinitionRenderState();
        var hidden = armorState.getHiddenStates();

        final PlayerModel playerModel;
        if (self() instanceof PlayerModel player) {
            playerModel = player;
        } else {
            playerModel = null;
        }

        final HumanoidModel<?> humanoidModel;
        if (self() instanceof HumanoidModel<?> humanoid) {
            humanoidModel = humanoid;
            humanoid.head.skipDraw = false;
            humanoid.body.skipDraw = false;
            humanoid.hat.skipDraw = false;
            humanoid.leftLeg.skipDraw = false;
            humanoid.leftArm.skipDraw = false;
            humanoid.rightLeg.skipDraw = false;
            humanoid.rightArm.skipDraw = false;
        } else {
            humanoidModel = null;
        }
        if (hidden == null) {
            return;
        }

        final PlayerCapeModel capeModel;
        if (self() instanceof PlayerCapeModel cape) {
            capeModel = cape;
        } else {
            capeModel = null;
        }

        for (var entry : BodyPart.getEntries()) {
            var state = hidden.get(entry);
            ModelPart modelPart = null;
            ModelPart overlayPart = null;
            if (humanoidModel != null) {
                var result = switch (entry) {
                    case HEAD -> humanoidModel.head;
                    case CHEST -> humanoidModel.body;
                    case LEFT_ARM -> humanoidModel.leftArm;
                    case RIGHT_ARM -> humanoidModel.rightArm;
                    case LEFT_LEG -> humanoidModel.leftLeg;
                    case RIGHT_LEG -> humanoidModel.rightLeg;
                    default -> null;
                };
                if (entry == BodyPart.HEAD) {
                    overlayPart = humanoidModel.hat;
                }
                if (result != null) {
                    modelPart = result;
                }
            } else if (capeModel != null) {
                modelPart = capeModel.cape;
            }

            if (playerModel != null) {
                var result = switch (entry) {
                    case HEAD -> playerModel.hat;
                    case CHEST -> playerModel.jacket;
                    case LEFT_ARM -> playerModel.leftSleeve;
                    case RIGHT_ARM -> playerModel.rightSleeve;
                    case LEFT_LEG -> playerModel.leftPants;
                    case RIGHT_LEG -> playerModel.rightLeg;
                    default -> null;
                };
                if (result != null) {
                    overlayPart = result;
                }
            }


            if (modelPart != null) {
                modelPart.skipDraw = state != null && state.getBase();
            }
            if (overlayPart != null) {
                overlayPart.skipDraw = state != null && state.getOverlay();
            }
        }
    }

}
