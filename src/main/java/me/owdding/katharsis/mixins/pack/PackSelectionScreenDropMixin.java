package me.owdding.katharsis.mixins.pack;

import me.owdding.katharsis.hooks.pack.PackDetectorHook;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.file.Path;

@Mixin(targets = "net.minecraft.client.gui.screens.packs.PackSelectionScreen$1")
public class PackSelectionScreenDropMixin implements PackDetectorHook<Path> {

    @Override
    public Path katharsis$createCatsPack(Path path) {
        return path;
    }
}
