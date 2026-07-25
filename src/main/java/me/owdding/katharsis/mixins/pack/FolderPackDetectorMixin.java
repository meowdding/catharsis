package me.owdding.katharsis.mixins.pack;

import me.owdding.katharsis.features.pack.CatsResourceSupplier;
import me.owdding.katharsis.hooks.pack.PackDetectorHook;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.file.Path;

@Mixin(targets = "net.minecraft.server.packs.repository.FolderRepositorySource$FolderPackDetector")
public class FolderPackDetectorMixin implements PackDetectorHook<Pack.ResourcesSupplier> {

    @Override
    public Pack.ResourcesSupplier katharsis$createCatsPack(Path path) {
        return new CatsResourceSupplier(path);
    }
}
