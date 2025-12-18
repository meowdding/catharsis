package me.owdding.catharsis.mixins.sounds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.blocks.BlockReplacements;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @WrapOperation(
        method = "playStepSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/SoundType;getStepSound()Lnet/minecraft/sounds/SoundEvent;"
        )
    )
    private SoundEvent catharsis$modifySteapsound(SoundType instance, Operation<SoundEvent> original, @Local(argsOnly = true) BlockPos pos, @Local BlockState blockState) {
        var step = BlockReplacements.getSound(blockState, pos).getStepSound();
        if (step == null) {
            return original.call(instance);
        }
        return step;
    }

}
