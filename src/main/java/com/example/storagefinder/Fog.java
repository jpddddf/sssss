package com.example.storagefinder;

/** Fog: własny kolor i dystans mgły (patrz mixin/FogRendererMixin). */
public final class Fog {
    private Fog() {}

    public static boolean active() {
        return StorageFinderConfig.fogEnabled && Krux.licensed();
    }

    public static float start() { return Math.min(StorageFinderConfig.fogStart, StorageFinderConfig.fogEnd - 5); }

    public static float end() { return Math.max(StorageFinderConfig.fogEnd, StorageFinderConfig.fogStart + 5); }
}
