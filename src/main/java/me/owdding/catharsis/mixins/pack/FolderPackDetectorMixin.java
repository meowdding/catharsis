package me.owdding.catharsis.mixins.pack;

import me.owdding.catharsis.hooks.pack.PackDetectorHook;
import me.owdding.catharsis.utils.tar.TarResourceSupplier;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.file.Path;

@Mixin(targets = "net.minecraft.server.packs.repository.FolderRepositorySource$FolderPackDetector")
public class FolderPackDetectorMixin implements PackDetectorHook<Pack.ResourcesSupplier> {

    @Override
    public Pack.ResourcesSupplier catharsis$createTarPack(Path path) {
        return new TarResourceSupplier(path);
    }
}
