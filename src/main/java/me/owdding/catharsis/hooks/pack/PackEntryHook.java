package me.owdding.catharsis.hooks.pack;

import me.owdding.catharsis.features.pack.config.PackConfigOption;
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection;

import java.util.List;

public interface PackEntryHook {

    default CatharsisMetadataSection catharsis$getMetadata() {
        throw new UnsupportedOperationException();
    }

    default List<PackConfigOption> catharsis$getConfig() {
        throw new UnsupportedOperationException();
    }

    default boolean catharsis$requiresPackToOpenConfig() {
        return false;
    }
}
