package me.owdding.catharsis.mixins.environment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.environment.DryFoliageColor;
import me.owdding.catharsis.features.environment.EnvironmentalModifiers;
import me.owdding.catharsis.features.environment.FoliageColor;
import me.owdding.catharsis.features.environment.GrassColor;
import me.owdding.catharsis.features.environment.WaterColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

    @WrapOperation(method = "getAverageGrassColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BiomeColors;getAverageColor(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"))
    private static int wrapGrassColor(BlockAndTintGetter level, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return EnvironmentalModifiers.wrap(level, pos, original.call(level, pos, colorResolver), () -> GrassColor.INSTANCE);
    }

    @WrapOperation(method = "getAverageFoliageColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BiomeColors;getAverageColor(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"))
    private static int wrapFoliageColor(BlockAndTintGetter level, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return EnvironmentalModifiers.wrap(level, pos, original.call(level, pos, colorResolver), () -> FoliageColor.INSTANCE);
    }

    @WrapOperation(method = "getAverageWaterColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BiomeColors;getAverageColor(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"))
    private static int wrapWaterColor(BlockAndTintGetter level, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return EnvironmentalModifiers.wrap(level, pos, original.call(level, pos, colorResolver), () -> WaterColor.INSTANCE);
    }

    @WrapOperation(method = "getAverageDryFoliageColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BiomeColors;getAverageColor(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"))
    private static int wrapDryFoliage(BlockAndTintGetter level, BlockPos pos, ColorResolver colorResolver, Operation<Integer> original) {
        return EnvironmentalModifiers.wrap(level, pos, original.call(level, pos, colorResolver), () -> DryFoliageColor.INSTANCE);
    }

}
