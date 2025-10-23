package me.owdding.catharsis.mixins;

import com.mojang.serialization.MapCodec;
import kotlin.Unit;
import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent;
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangeSelectItemModelProperties.class)
public class RangeSelectItemModelPropertiesMixin {

    @Shadow
    @Final
    public static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends RangeSelectItemModelProperty>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void bootstrap(CallbackInfo ci) {
        new BootstrapNumericPropertiesEvent((key, value) -> {
            ID_MAPPER.put(key, value);
            return Unit.INSTANCE;
        });
    }

}
