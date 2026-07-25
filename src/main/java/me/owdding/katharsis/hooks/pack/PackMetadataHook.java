package me.owdding.katharsis.hooks.pack;

import me.owdding.katharsis.features.pack.config.PackConfigOption;
import me.owdding.katharsis.features.pack.meta.KatharsisMetadataSection;

import java.util.List;

public interface PackMetadataHook extends PackEntryHook {

    default void katharsis$setMetadata(KatharsisMetadataSection metadata) {
        throw new UnsupportedOperationException();
    }

    default void katharsis$setConfig(List<PackConfigOption> config) {
        throw new UnsupportedOperationException();
    }
}
