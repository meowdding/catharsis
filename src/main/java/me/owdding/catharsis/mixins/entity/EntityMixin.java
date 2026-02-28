package me.owdding.catharsis.mixins.entity;

import me.owdding.catharsis.features.entity.CustomEntityDefinitions;
import me.owdding.catharsis.features.entity.models.CustomEntityModel;
import me.owdding.catharsis.features.entity.models.CustomEntityModels;
import me.owdding.catharsis.hooks.entity.EntityHook;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements EntityHook {

    @Unique
    private boolean catharsis$hasComputedModel = false;
    @Unique
    private CustomEntityModel catharsis$computedReplacement = null;

    @Override
    public void catharsis$resetCustomModel() {
        catharsis$hasComputedModel = false;
    }

    @Override
    public CustomEntityModel catharsis$getCustomEntityModel() {
        if (catharsis$hasComputedModel) {
            return catharsis$computedReplacement;
        }

        var customEntity = CustomEntityDefinitions.getFor((Entity) (Object) this);

        catharsis$hasComputedModel = true;

        CustomEntityModel customModel = null;

        if (customEntity != null) {
            customModel = CustomEntityModels.getModel(customEntity);
        }


        catharsis$computedReplacement = customModel;

        return customModel;
    }
}
