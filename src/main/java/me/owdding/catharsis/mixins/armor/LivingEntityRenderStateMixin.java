package me.owdding.catharsis.mixins.armor;

import me.owdding.catharsis.features.armor.ArmorDefinitionRenderState;
import me.owdding.catharsis.hooks.armor.LivingEntityRenderStateHook;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements LivingEntityRenderStateHook {

    @Unique
    private final ArmorDefinitionRenderState catharsis$armorDefinitionRenderState = new ArmorDefinitionRenderState();

    @Override
    public ArmorDefinitionRenderState catharsis$getArmorDefinitionRenderState() {
        return catharsis$armorDefinitionRenderState;
    }
}
