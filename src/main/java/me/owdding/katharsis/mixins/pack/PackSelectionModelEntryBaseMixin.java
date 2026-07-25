package me.owdding.katharsis.mixins.pack;

import me.owdding.katharsis.features.pack.config.PackConfigOption;
import me.owdding.katharsis.features.pack.meta.KatharsisMetadataSection;
import me.owdding.katharsis.hooks.pack.PackEntryHook;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(targets = "net/minecraft/client/gui/screens/packs/PackSelectionModel$EntryBase")
public class PackSelectionModelEntryBaseMixin implements PackEntryHook {

    @Shadow
    @Final
    private Pack pack;

    @Override
    public KatharsisMetadataSection katharsis$getMetadata() {
        if (this.pack != null) {
            return this.pack.katharsis$getMetadata();
        }
        return null;
    }

    @Override
    public List<PackConfigOption> katharsis$getConfig() {
        if (this.pack != null) {
            return this.pack.katharsis$getConfig();
        }
        return null;
    }

    @Override
    public boolean katharsis$requiresPackToOpenConfig() {
        return this.pack != null && this.pack.katharsis$requiresPackToOpenConfig();
    }
}
