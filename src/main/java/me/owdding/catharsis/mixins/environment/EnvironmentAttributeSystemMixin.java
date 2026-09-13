package me.owdding.catharsis.mixins.environment;

import me.owdding.catharsis.features.environment.EnvironmentalModifiers;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {

    @Inject(method = "addDefaultLayers", at = @At("TAIL"))
    private static void forLevel(EnvironmentAttributeSystem.Builder builder, Level level, CallbackInfo ci) {
        EnvironmentalModifiers.addLayers(builder);
    }

}
