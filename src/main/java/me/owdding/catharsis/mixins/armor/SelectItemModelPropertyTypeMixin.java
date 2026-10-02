package me.owdding.catharsis.mixins.armor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.owdding.catharsis.features.armor.models.SelectArmorModel;
import me.owdding.catharsis.features.entity.conditions.SelectEquipmentEntityConditionSwitch;
import me.owdding.catharsis.features.tooltip.models.SelectTooltipDefinition;
import me.owdding.catharsis.hooks.armor.SelectItemModelPropertyTypeHook;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(SelectItemModelProperty.Type.class)
public class SelectItemModelPropertyTypeMixin<P extends SelectItemModelProperty<T>, T> implements SelectItemModelPropertyTypeHook<P, T> {

    @Unique
    private static final Map<Object, MapCodec<?>> catharsis$armorCodecs = new IdentityHashMap<>();
    @Unique
    private static final Map<Object, MapCodec<?>> catharsis$tooltipCodecs = new IdentityHashMap<>();
    @Unique
    private static final Map<Object, MapCodec<?>> catharsis$equipmentCodecs = new IdentityHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<SelectArmorModel.UnbakedSwitch<P, T>> catharsis$getArmorSwitchCodec() {
        return (MapCodec<SelectArmorModel.UnbakedSwitch<P, T>>) catharsis$armorCodecs.get(this);
    }

    @Override
    public void catharsis$setArmorSwitchCodec(MapCodec<SelectArmorModel.UnbakedSwitch<P, T>> codec) {
        catharsis$armorCodecs.put(this, codec);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>> catharsis$getTooltipSwitchCodec() {
        return (MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>>) catharsis$tooltipCodecs.get(this);
    }

    @Override
    public void catharsis$setTooltipSwitchCodec(MapCodec<SelectTooltipDefinition.UnbakedSwitch<P, T>> codec) {
        catharsis$tooltipCodecs.put(this, codec);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<SelectEquipmentEntityConditionSwitch<P, T>> catharsis$getEquipmentSwitchCodec() {
        return (MapCodec<SelectEquipmentEntityConditionSwitch<P, T>>) catharsis$equipmentCodecs.get(this);
    }

    @Override
    public void catharsis$setEquipmentSwitchCodec(MapCodec<SelectEquipmentEntityConditionSwitch<P, T>> codec) {
        catharsis$equipmentCodecs.put(this, codec);
    }

    @Inject(method = "create", at = @At("RETURN"))
    private static <P extends SelectItemModelProperty<T>, T> void onCreate(
        MapCodec<P> propertyCodec,
        Codec<T> valueCodec,
        CallbackInfoReturnable<SelectItemModelProperty.Type<P, T>> cir
    ) {
        SelectItemModelProperty.Type<P, T> type = cir.getReturnValue();

        var armorCasesCodec = SelectArmorModel.UnbakedSwitch.createCasesFieldCodec(valueCodec);
        catharsis$armorCodecs.put(type, RecordCodecBuilder.<SelectArmorModel.UnbakedSwitch<P, T>>mapCodec(
            instance -> instance.group(
                    propertyCodec.forGetter(SelectArmorModel.UnbakedSwitch::getProperty),
                    armorCasesCodec.forGetter(SelectArmorModel.UnbakedSwitch::getCases)
                )
                .apply(instance, SelectArmorModel.UnbakedSwitch::new)
        ));

        var tooltipCasesCodec = SelectTooltipDefinition.UnbakedSwitch.createCasesFieldCodec(valueCodec);
        catharsis$tooltipCodecs.put(type, RecordCodecBuilder.<SelectTooltipDefinition.UnbakedSwitch<P, T>>mapCodec(
            instance -> instance.group(
                    propertyCodec.forGetter(SelectTooltipDefinition.UnbakedSwitch::getProperty),
                    tooltipCasesCodec.forGetter(SelectTooltipDefinition.UnbakedSwitch::getCases)
                )
                .apply(instance, SelectTooltipDefinition.UnbakedSwitch::new)
        ));

        var equipmentCasesCodec = SelectEquipmentEntityConditionSwitch.createCasesFieldCodec(valueCodec);
        catharsis$equipmentCodecs.put(type, RecordCodecBuilder.<SelectEquipmentEntityConditionSwitch<P, T>>mapCodec(
            instance -> instance.group(
                    propertyCodec.forGetter(SelectEquipmentEntityConditionSwitch::getProperty),
                    equipmentCasesCodec.forGetter(SelectEquipmentEntityConditionSwitch::getCases)
                )
                .apply(instance, SelectEquipmentEntityConditionSwitch::new)
        ));
    }
}
