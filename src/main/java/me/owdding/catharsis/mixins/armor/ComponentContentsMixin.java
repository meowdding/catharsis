package me.owdding.catharsis.mixins.armor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import me.owdding.catharsis.features.armor.models.SelectArmorModel;
import me.owdding.catharsis.features.tooltip.models.SelectTooltipDefinition;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings({"rawtypes", "Convert2MethodRef", "unchecked"})
@Mixin(ComponentContents.class)
public class ComponentContentsMixin {

    @ModifyReturnValue(method = "createType", at = @At("RETURN"))
    private static <T> SelectItemModelProperty.Type<ComponentContents<T>, T> createType(
        SelectItemModelProperty.Type<ComponentContents<T>, T> original,
        @Local(ordinal = 0) Codec<DataComponentType> typeCodec
    ) {
        // noinspection RedundantCast
        MapCodec<SelectArmorModel.UnbakedSwitch<ComponentContents<T>, T>> armorCodec = typeCodec.dispatchMap(
            "component",
            unbakedSwitch -> ((SelectArmorModel.UnbakedSwitch<ComponentContents<T>, T>) unbakedSwitch).getProperty().componentType(),
            type -> catharsis$createCodec(type)
        );
        original.catharsis$setArmorSwitchCodec(armorCodec);

        // noinspection RedundantCast
        MapCodec<SelectTooltipDefinition.UnbakedSwitch<ComponentContents<T>, T>> tooltipCodec = typeCodec.dispatchMap(
            "component",
            unbakedSwitch -> ((SelectTooltipDefinition.UnbakedSwitch<ComponentContents<T>, T>) unbakedSwitch).getProperty().componentType(),
            type -> catharsis$createCodec(type)
        );
        original.catharsis$setTooltipSwitchCodec(tooltipCodec);

        return original;
    }

    @Unique
    private static <T> MapCodec<SelectArmorModel.UnbakedSwitch<ComponentContents<T>, T>> catharsis$createCodec(DataComponentType<T> type) {
        return SelectArmorModel.UnbakedSwitch.createCasesFieldCodec(type.codecOrThrow()).xmap(
            list -> new SelectArmorModel.UnbakedSwitch<>(new ComponentContents<>(type), list),
            SelectArmorModel.UnbakedSwitch::getCases
        );
    }
}
