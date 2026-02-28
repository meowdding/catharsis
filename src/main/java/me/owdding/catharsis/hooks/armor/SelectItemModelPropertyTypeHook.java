package me.owdding.catharsis.hooks.armor;

import com.mojang.serialization.MapCodec;
import me.owdding.catharsis.features.armor.models.SelectArmorModel;
import me.owdding.catharsis.features.tooltip.models.SelectTooltipDefinition;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;

public interface SelectItemModelPropertyTypeHook<P extends SelectItemModelProperty<T>, T> {

    default MapCodec<SelectArmorModel.UnbakedSwitch<P, T>> catharsis$getArmorSwitchCodec() {
        throw new UnsupportedOperationException();
    }

    default void catharsis$setArmorSwitchCodec(MapCodec<SelectArmorModel.UnbakedSwitch<P, T>> codec) {
        throw new UnsupportedOperationException();
    }


    default MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>> catharsis$getTooltipSwitchCodec() {
        throw new UnsupportedOperationException();
    }

    default void catharsis$setTooltipSwitchCodec(MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>> codec) {
        throw new UnsupportedOperationException();
    }
}
