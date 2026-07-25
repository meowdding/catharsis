package me.owdding.katharsis.hooks.pack;

import java.nio.file.Path;

public interface PackDetectorHook<T> {

    T katharsis$createCatsPack(Path path);
}
