package me.owdding.catharsis.mixins.environment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.environment.DryFoliageColor;
import me.owdding.catharsis.features.environment.EnvironmentalModifiers;
import me.owdding.catharsis.features.environment.FoliageColor;
import me.owdding.catharsis.features.environment.GrassColor;
import me.owdding.catharsis.features.environment.WaterColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @WrapOperation(
        method = "lambda$new$1", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;calculateBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"
    )
    )
    private static int getGrassColor(ClientLevel instance, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return original.call(instance, pos, EnvironmentalModifiers.wrap(instance, pos, colorResolver, () -> GrassColor.INSTANCE));
    }

    @WrapOperation(
        method = "lambda$new$3", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;calculateBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"
    )
    )
    private static int getDryFoliageColor(ClientLevel instance, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return original.call(instance, pos, EnvironmentalModifiers.wrap(instance, pos, colorResolver, () -> DryFoliageColor.INSTANCE));
    }

    @WrapOperation(
        method = "lambda$new$4", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;calculateBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"
    )
    )
    private static int getWaterColor(ClientLevel instance, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return original.call(instance, pos, EnvironmentalModifiers.wrap(instance, pos, colorResolver, () -> WaterColor.INSTANCE));
    }

    @WrapOperation(
        method = "lambda$new$2", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;calculateBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"
    )
    )
    private static int getFoliageColor(ClientLevel instance, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return original.call(instance, pos, EnvironmentalModifiers.wrap(instance, pos, colorResolver, () -> FoliageColor.INSTANCE));
    }

}
