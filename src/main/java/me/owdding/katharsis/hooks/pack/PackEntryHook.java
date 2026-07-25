package me.owdding.katharsis.hooks.pack;

import me.owdding.katharsis.features.pack.config.PackConfigOption;
import me.owdding.katharsis.features.pack.meta.KatharsisMetadataSection;

import java.util.List;

public interface PackEntryHook {

    default KatharsisMetadataSection katharsis$getMetadata() {
        throw new UnsupportedOperationException();
    }

    default List<PackConfigOption> katharsis$getConfig() {
        throw new UnsupportedOperationException();
    }

    default boolean katharsis$requiresPackToOpenConfig() {
        return false;
    }
}
