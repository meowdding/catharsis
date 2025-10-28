package me.owdding.catharsis.mixins.pack;

import me.owdding.catharsis.hooks.pack.PackDetectorHook;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.file.Path;

@Mixin(targets = "net.minecraft.client.gui.screens.packs.PackSelectionScreen$1")
public class PackSelectionScreenDropMixin implements PackDetectorHook<Path> {

    @Override
    public Path catharsis$createTarPack(Path path) {
        return path;
    }
}
