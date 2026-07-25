package me.owdding.katharsis.mixins.entity;

import me.owdding.katharsis.features.entity.CustomEntityDefinitions;
import me.owdding.katharsis.features.entity.models.CustomEntityModel;
import me.owdding.katharsis.features.entity.models.CustomEntityModels;
import me.owdding.katharsis.hooks.entity.EntityHook;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements EntityHook {

    @Unique
    private boolean katharsis$hasComputedModel = false;
    @Unique
    private CustomEntityModel katharsis$computedReplacement = null;
    @Unique
    private PlayerSkin katharsis$lastRenderedPlayerSkin = null;
    @Unique
    private int katharsis$modelRevision = -1;

    @Override
    public void katharsis$resetCustomModel() {
        katharsis$hasComputedModel = false;
    }

    @Override
    public CustomEntityModel katharsis$getCustomEntityModel() {
        if (this instanceof ClientAvatarEntity car) {
            PlayerSkin currentSkin = car.getSkin();
            if (currentSkin != katharsis$lastRenderedPlayerSkin) {
                katharsis$resetCustomModel();
                katharsis$lastRenderedPlayerSkin = currentSkin;
            }
        }

        if (katharsis$hasComputedModel && katharsis$modelRevision == CustomEntityModels.getRevision()) {
            return katharsis$computedReplacement;
        }

        var customEntity = CustomEntityDefinitions.getFor((Entity) (Object) this);

        CustomEntityModel customModel = null;

        if (customEntity != null) {
            customModel = CustomEntityModels.getModel(customEntity);
        }

        katharsis$computedReplacement = customModel;
        katharsis$hasComputedModel = true;
        katharsis$modelRevision = CustomEntityModels.getRevision();

        return customModel;
    }

    @Inject(
        method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V",
        at = @At("TAIL")
    )
    private void onSyncedDataUpdate(EntityDataAccessor<?> accessor, CallbackInfo ci) {
        katharsis$resetCustomModel();
    }
}
