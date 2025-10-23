package me.owdding.catharsis.mixins;

import com.mojang.serialization.MapCodec;
import kotlin.Unit;
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent;
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent;
import me.owdding.catharsis.features.properties.DataTypeProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectItemModelProperties.class)
public class SelectItemModelPropertiesMixin {

    @Shadow
    @Final
    public static ExtraCodecs.LateBoundIdMapper<ResourceLocation, SelectItemModelProperty.Type<?, ?>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void bootstrap(CallbackInfo ci) {
        new BootstrapSelectPropertiesEvent((key, value) -> {
            ID_MAPPER.put(key, value);
            return Unit.INSTANCE;
        });
    }

}
