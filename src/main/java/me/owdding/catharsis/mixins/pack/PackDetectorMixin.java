package me.owdding.catharsis.mixins.pack;

import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.hooks.pack.PackDetectorHook;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;

@Mixin(PackDetector.class)
public class PackDetectorMixin implements PackDetectorHook<Object> {

    @Inject(
        at = @At(value = "INVOKE", target = "Ljava/nio/file/attribute/BasicFileAttributes;isRegularFile()Z"),
        method = "detectPackResources",
        cancellable = true
    )
    public void addTar(Path path, List<ForbiddenSymlinkInfo> forbiddenSymlinkInfos, CallbackInfoReturnable<Object> cir, @Local(index = 3) Path path2) {
        if (path2.getFileName().toString().endsWith(".tar.gz")) {
            cir.setReturnValue(this.catharsis$createTarPack(path2));
        }
    }

    @Override
    public Object catharsis$createTarPack(Path path) {
        return null;
    }
}
