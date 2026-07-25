package me.owdding.katharsis.mixins.pack;

import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.katharsis.hooks.pack.PackDetectorHook;
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
        var fileName = path2.getFileName().toString();
        if (fileName.endsWith(".cats")) {
            cir.setReturnValue(this.katharsis$createCatsPack(path2));
        } else if (fileName.contains(".cats") && fileName.endsWith(".zip")) {
            cir.setReturnValue(this.katharsis$createCatsPack(path2));
        }
    }

    @Override
    public Object katharsis$createCatsPack(Path path) {
        return null;
    }
}
