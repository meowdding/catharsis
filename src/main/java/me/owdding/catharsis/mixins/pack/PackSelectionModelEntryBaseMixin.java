package me.owdding.catharsis.mixins.pack;

import me.owdding.catharsis.features.pack.config.PackConfigOption;
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection;
import me.owdding.catharsis.hooks.pack.PackEntryHook;
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
    public CatharsisMetadataSection catharsis$getMetadata() {
        if (this.pack != null) {
            return this.pack.catharsis$getMetadata();
        }
        return null;
    }

    @Override
    public List<PackConfigOption> catharsis$getConfig() {
        if (this.pack != null) {
            return this.pack.catharsis$getConfig();
        }
        return null;
    }

    @Override
    public boolean catharsis$requiresPackToOpenConfig() {
        return this.pack != null && this.pack.catharsis$requiresPackToOpenConfig();
    }
}
