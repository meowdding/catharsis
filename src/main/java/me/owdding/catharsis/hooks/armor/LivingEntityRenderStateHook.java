package me.owdding.catharsis.hooks.armor;

import me.owdding.catharsis.features.armor.ArmorDefinitionRenderState;

public interface LivingEntityRenderStateHook {

    boolean catharsis$getAndSetFirstDraw();

    ArmorDefinitionRenderState catharsis$getArmorDefinitionRenderState();
}
