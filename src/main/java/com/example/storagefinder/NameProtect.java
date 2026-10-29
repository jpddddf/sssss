package com.example.storagefinder;

import net.minecraft.client.Minecraft;

/**
 * Name Protect: podmienia Twój prawdziwy nick na fałszywy w tekście wyświetlanym przez grę
 * (czat, tab, nametagi, scoreboard...). Działa tylko lokalnie — serwer i inni gracze widzą Twój prawdziwy nick.
 * Fałszywy nick jest dokładnie tym, co wpiszesz (bez powtarzania i dopełniania).
 */
public final class NameProtect {
    private static String cachedReal = "";
    private static String cachedSrc = "";
    private static String cachedFake = "";

    private NameProtect() {}

    public static String apply(String text) {
        try {
            if (!StorageFinderConfig.npEnabled || text == null || text.length() < 3 || !Krux.licensed()) return text;
            String real = realName();
            if (real.isEmpty() || !text.contains(real)) return text;
            return text.replace(real, fake(real));
        } catch (Throwable t) {
            return text;
        }
    }

    private static String realName() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return "";
        var user = mc.getUser();
        return user == null ? "" : user.getName();
    }

    private static String fake(String real) {
        String src = StorageFinderConfig.npName == null ? "" : StorageFinderConfig.npName.trim();
        if (src.isEmpty()) src = "Krux";
        if (real.equals(cachedReal) && src.equals(cachedSrc)) return cachedFake;

        cachedReal = real;
        cachedSrc = src;
        cachedFake = src;
        return cachedFake;
    }
}
