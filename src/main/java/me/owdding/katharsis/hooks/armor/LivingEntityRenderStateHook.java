package me.owdding.katharsis.hooks.armor;

import me.owdding.katharsis.features.armor.ArmorDefinitionRenderState;

public interface LivingEntityRenderStateHook {

    default boolean katharsis$getAndSetFirstDraw() {
        throw new UnsupportedOperationException();
    }

    default ArmorDefinitionRenderState katharsis$getArmorDefinitionRenderState() {
        throw new UnsupportedOperationException();
    }
}
