package me.owdding.catharsis.mixins.sounds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.blocks.BlockReplacements;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelEventHandler.class)
public class LevelEventHandlerMixin {

    @WrapOperation(
        method = "levelEvent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/SoundType;getBreakSound()Lnet/minecraft/sounds/SoundEvent;"
        )
    )
    private SoundEvent catharsis$modifyBlockSoundType(SoundType instance, Operation<SoundEvent> original, @Local(argsOnly = true) BlockPos pos, @Local BlockState blockState) {
        var breakSound = BlockReplacements.getSound(blockState, pos).getBreakSound();
        if (breakSound == null) {
            return original.call(instance);
        }
        return breakSound;
    }

}
