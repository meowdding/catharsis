package me.owdding.catharsis.mixins.sounds;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.owdding.catharsis.features.blocks.BlockReplacements;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyExpressionValue(
        method = "playBlockFallSound",
        at = @At(value = "NEW", target = "(III)Lnet/minecraft/core/BlockPos;")
    )
    public BlockPos catharsis$blockpos(BlockPos original, @Share("position") LocalRef<BlockPos> blockPos) {
        blockPos.set(original);
        return original;
    }

    @WrapOperation(
        method = "playBlockFallSound",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SoundType;getFallSound()Lnet/minecraft/sounds/SoundEvent;")
    )
    public SoundEvent catharsis$modifySound(SoundType instance, Operation<SoundEvent> original, @Share("position") LocalRef<BlockPos> blockPos, @Local BlockState blockState) {
        var fall = BlockReplacements.getSound(blockState, blockPos.get()).getFallSound();
        if (fall == null) {
            return original.call(instance);
        }
        return fall;
    }


}
