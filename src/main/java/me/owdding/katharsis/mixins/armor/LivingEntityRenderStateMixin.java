package me.owdding.katharsis.mixins.armor;

import me.owdding.katharsis.features.armor.ArmorDefinitionRenderState;
import me.owdding.katharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements LivingEntityRenderStateHook {

    @Unique
    private final ArmorDefinitionRenderState katharsis$armorDefinitionRenderState = new ArmorDefinitionRenderState();

    @Unique
    private int katharsis$drawCount = 0;

    @Override
    public boolean katharsis$getAndSetFirstDraw() {
        // So minecraft calls setupAnim once for no reason and then recalls it again in submitModel
        if (katharsis$drawCount <= 1) {
            katharsis$drawCount++;
            return true;
        }
        return false;
    }

    @Override
    public ArmorDefinitionRenderState katharsis$getArmorDefinitionRenderState() {
        return katharsis$armorDefinitionRenderState;
    }
}
