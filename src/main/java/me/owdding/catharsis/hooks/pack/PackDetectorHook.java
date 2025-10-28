package me.owdding.catharsis.hooks.pack;

import java.nio.file.Path;

public interface PackDetectorHook<T> {

    T catharsis$createTarPack(Path path);
}
