package me.owdding.catharsis.mixins.environment;

//~ if >= 26.3 'addDefaultLayers' -> 'addStaticLayers', 'Level' -> 'LevelAccessor' {
import me.owdding.catharsis.features.environment.EnvironmentalModifiers;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {

    @Inject(method = "addStaticLayers", at = @At("TAIL"))
    private static void forLevelAccessor(EnvironmentAttributeSystem.Builder builder, LevelAccessor level, CallbackInfo ci) {
        EnvironmentalModifiers.addLayers(builder);
    }

}
//~}
