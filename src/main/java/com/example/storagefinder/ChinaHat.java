package com.example.storagefinder;

import net.minecraft.client.Minecraft;

/** China Hat: stożkowy kapelusz nad głową (rysowany w StorageRenderer). */
public final class ChinaHat {
    private ChinaHat() {}

    public static boolean visible(Minecraft mc) {
        if (!StorageFinderConfig.xb("m.chinahat", false) || !Krux.licensed() || mc.player == null) return false;
        return !mc.options.getCameraType().isFirstPerson() || StorageFinderConfig.xb("ch.first", false);
    }
}
