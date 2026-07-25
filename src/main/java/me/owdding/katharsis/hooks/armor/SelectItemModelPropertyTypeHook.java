package me.owdding.katharsis.hooks.armor;

import com.mojang.serialization.MapCodec;
import me.owdding.katharsis.features.armor.models.SelectArmorModel;
import me.owdding.katharsis.features.entity.conditions.SelectEquipmentEntityConditionSwitch;
import me.owdding.katharsis.features.tooltip.models.SelectTooltipDefinition;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;

public interface SelectItemModelPropertyTypeHook<P extends SelectItemModelProperty<T>, T> {

    default MapCodec<SelectArmorModel.UnbakedSwitch<P, T>> katharsis$getArmorSwitchCodec() {
        throw new UnsupportedOperationException();
    }

    default void katharsis$setArmorSwitchCodec(MapCodec<SelectArmorModel.UnbakedSwitch<P, T>> codec) {
        throw new UnsupportedOperationException();
    }


    default MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>> katharsis$getTooltipSwitchCodec() {
        throw new UnsupportedOperationException();
    }

    default void katharsis$setTooltipSwitchCodec(MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>> codec) {
        throw new UnsupportedOperationException();
    }

    default MapCodec<SelectEquipmentEntityConditionSwitch<P, T>> katharsis$getEquipmentSwitchCodec() {
        throw new UnsupportedOperationException();
    }

    default void katharsis$setEquipmentSwitchCodec(MapCodec<SelectEquipmentEntityConditionSwitch<P, T>> codec) {
        throw new UnsupportedOperationException();
    }
}
