package com.example.storagefinder;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

public final class StorageFinderConfig {
    // ---- Storage Finder ----
    public static boolean enabled = false;
    public static final boolean[] types = new boolean[StorageType.values().length];
    public static boolean tracers = true;          // tracery do bloków
    public static int radiusChunks = 8;
    public static boolean susEnabled = false;      // moduł Sus Chunk: czerwony chunk przy ogromnej ilości wyrośniętego ametystu
    public static int susRange = 8;                // zasięg skanowania Sus Chunk (chunki)
    public static boolean susTracers = true;       // linia do sus chunka
    public static int susThreshold = 5;            // ile wyrośniętych kryształów ametystu w chunku = "sus" (0 = już od 1 kryształu)
    public static boolean susNotify = true;        // komunikat na czacie

    // ---- ESP ----
    public static boolean espEnabled = false;
    public static boolean espPlayers = true;
    public static boolean espHostile = true;
    public static boolean espPassive = false;
    public static int espPlayerColor = 0x3DD6FF;
    public static int espMobColor = 0xFF5555;
    public static int espOpacity = 50;             // %

    // ---- Tracers (gracze / moby) ----
    public static boolean tracersEnabled = false;
    public static boolean trPlayers = true;
    public static boolean trHostile = false;
    public static boolean trPassive = false;
    public static double tracerThickness = 1.0;

    // ---- ArrayList ----
    public static boolean arrayList = true;
    public static boolean alBackground = true;
    public static boolean alSuffix = true;
    public static int alColorMode = 2;             // 0 Static, 1 Rainbow, 2 Gradient
    public static int alSort = 0;                  // 0 Length, 1 A-Z
    public static int accent = 0x4FB3E8;
    public static int alAccent = 0x4FB3E8;   // kolor ArrayList/HUD (niezależny od GUI)
    // ---- ogólny magazyn ustawień nowych modułów (klucze "x.*" w pliku) ----
    public static final java.util.concurrent.ConcurrentHashMap<String, Double> XN = new java.util.concurrent.ConcurrentHashMap<>();
    public static double xd(String k, double d) { Double v = XN.get(k); return v == null ? d : v; }
    public static int xi(String k, int d) { return (int) Math.round(xd(k, d)); }
    public static boolean xb(String k, boolean d) { Double v = XN.get(k); return v == null ? d : v != 0.0; }
    public static void xset(String k, double v) { XN.put(k, v); }
    public static void xsetb(String k, boolean v) { XN.put(k, v ? 1.0 : 0.0); }
    // ---- Target ESP / Fog ----
    public static int auraEffect = 1;              // 0 None, 1 Ring, 2 Box, 3 Halo, 4 Orbit, 5 Helix
    public static int auraEffectColor = 0x4FB3E8;
    public static boolean targetHud = false;       // "Target ESP" — prostokąt z nazwą/HP/pancerzem celu Aury
    public static int targetHudX = -1;
    public static int targetHudY = -1;
    public static boolean fogEnabled = false;
    public static int fogStart = 20;
    public static int fogEnd = 80;
    public static int fogColor = 0xB8C4D0;
    // ---- styl GUI ----
    public static int guiSize = 100;      // % skali okna
    public static int guiWidth = 100;     // % szerokości okna
    public static int guiHeight = 100;    // % wysokości okna
    public static int guiRound = 26;      // zaokrąglenie rogów
    public static int guiBg = 0x15181C;   // kolor tła
    public static int guiText = 0xF4F7FA; // kolor tekstu

    // ---- nowe moduły ----
    public static int zoomFov = 30;
    public static boolean npEnabled = false;
    public static String npName = "Krux";
    public static boolean infoEnabled = false;
    public static boolean infoCoords = true;
    public static boolean infoFps = true;
    public static boolean infoDir = true;

    // ---- ulubione moduły (gwiazdki w GUI) ----
    public static final Set<String> favorites = new LinkedHashSet<>();

    // ---- Freecam / Aura ----
    public static double freecamSpeed = 3.0;
    public static double auraRange = 3.0;
    public static double auraSpeed = 8.0;
    public static boolean auraPlayersOnly = true;
    public static boolean auraCooldown = true;
    public static boolean auraWalls = false;
    public static boolean auraRotate = false;
    public static int auraHitMode = 3;              // 0 Always, 1 Ground, 2 Crit, 3 Ground / Crit
    public static int auraPriority = 0;            // 0 Distance, 1 Health, 2 Crosshair
    public static int auraMinCharge = 91;          // minimalne naładowanie ataku w % (krytyk wymaga > 90)
    public static double auraCritSens = 2.0;       // czułość krytyków 1..5
    public static boolean auraStopSprint = true;   // zwolnij sprint w trybach z krytykiem
    public static boolean auraSilent = true;        // cicha rotacja (kamera się nie obraca)
    public static boolean auraRandomDelay = false;
    public static double auraRotSpeed = 40.0;      // maks. obrót na tick (stopnie)
    public static double auraJitter = 0.15;
    // ---- Aura (port Kill Aura) ----
    public static double auraSeek = 4.2;           // zasięg wyszukiwania/obracania się na cel
    public static double auraMinCps = 9.0;
    public static double auraMaxCps = 13.0;
    public static int auraRotMode = 0;             // 0 Regular, 1 Polar, 2 Snap, 3 None
    public static boolean auraPredict = true;
    public static double auraMinRot = 3.0;
    public static double auraMaxRot = 7.0;
    public static boolean auraRaycast = true;
    public static double auraMissRate = 0.0;       // % ciosów, które celowo pomijamy
    public static boolean auraKeepSprint = false;
    public static boolean auraTeams = false;
    // ---- Auto Armor / Auto Tool ----
    public static int armorMode = 0;               // 0 Open, 1 Spoof
    public static int armorStartDelay = 150;
    public static int armorSpeed = 150;
    public static boolean autoToolSneak = false;
    public static boolean autoToolSword = true;

    private static int lastSig = 0;

    static {
        Arrays.fill(types, true);
    }

    private StorageFinderConfig() {}

    public static boolean isOn(StorageType t) {
        return types[t.ordinal()];
    }

    public static void setOn(StorageType t, boolean v) {
        types[t.ordinal()] = v;
    }

    public static void toggle(StorageType t) {
        types[t.ordinal()] = !types[t.ordinal()];
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("storagefinder.properties");
    }

    private static boolean b(Properties p, String k, boolean d) {
        String v = p.getProperty(k);
        return v == null ? d : Boolean.parseBoolean(v);
    }

    private static double dbl(Properties p, String k, double d) {
        try { return Double.parseDouble(p.getProperty(k, Double.toString(d))); } catch (Exception e) { return d; }
    }

    private static int num(Properties p, String k, int d) {
        try { return Integer.parseInt(p.getProperty(k, Integer.toString(d))); } catch (Exception e) { return d; }
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(f)) {
                p.load(in);
            }
            enabled = b(p, "enabled", false);
            for (String key : p.stringPropertyNames()) {
                if (key.startsWith("x.")) {
                    try { XN.put(key.substring(2), Double.parseDouble(p.getProperty(key))); } catch (Exception ignored) {}
                }
            }
            for (StorageType t : StorageType.values()) {
                types[t.ordinal()] = b(p, "type." + t.name(), true);
            }
            tracers = b(p, "tracers", true);
            radiusChunks = Math.max(2, Math.min(16, num(p, "radius", 8)));
            susEnabled = b(p, "susEnabled", false);
            susRange = Math.max(2, Math.min(16, num(p, "susRange", 8)));
            susTracers = b(p, "susTracers", true);
            susThreshold = Math.max(0, Math.min(500, num(p, "susThr", 5)));
            susNotify = b(p, "susNotify", true);

            espEnabled = b(p, "espEnabled", false);
            espPlayers = b(p, "espPlayers", true);
            espHostile = b(p, "espHostile", true);
            espPassive = b(p, "espPassive", false);
            espPlayerColor = num(p, "espPlayerColor", 0x3DD6FF) & 0xFFFFFF;
            espMobColor = num(p, "espMobColor", 0xFF5555) & 0xFFFFFF;
            espOpacity = Math.max(10, Math.min(100, num(p, "espOpacity", 50)));

            tracersEnabled = b(p, "tracersEnabled", false);
            trPlayers = b(p, "trPlayers", true);
            trHostile = b(p, "trHostile", false);
            trPassive = b(p, "trPassive", false);
            tracerThickness = dbl(p, "tracerThickness", 1.0);

            arrayList = b(p, "arrayList", true);
            alBackground = b(p, "alBackground", true);
            alSuffix = b(p, "alSuffix", true);
            alColorMode = Math.max(0, Math.min(2, num(p, "alColorMode", 2)));
            alSort = Math.max(0, Math.min(1, num(p, "alSort", 0)));
            accent = num(p, "accent", 0x4FB3E8) & 0xFFFFFF;
            alAccent = num(p, "alAccent", accent) & 0xFFFFFF;
            auraEffect = Math.max(0, Math.min(5, num(p, "auraEffect", 1)));
            auraEffectColor = num(p, "auraEffectColor", 0x4FB3E8) & 0xFFFFFF;
            targetHud = b(p, "targetHud", false);
            targetHudX = num(p, "targetHudX", -1);
            targetHudY = num(p, "targetHudY", -1);
            fogEnabled = b(p, "fogEnabled", false);
            fogStart = Math.max(0, Math.min(150, num(p, "fogStart", 20)));
            fogEnd = Math.max(10, Math.min(300, num(p, "fogEnd", 80)));
            fogColor = num(p, "fogColor", 0xB8C4D0) & 0xFFFFFF;
            guiSize = Math.max(60, Math.min(130, num(p, "guiSize", 100)));
            guiWidth = Math.max(70, Math.min(120, num(p, "guiWidth", 100)));
            guiHeight = Math.max(70, Math.min(120, num(p, "guiHeight", 100)));
            guiRound = Math.max(0, Math.min(40, num(p, "guiRound", 26)));
            guiBg = num(p, "guiBg", 0x15181C) & 0xFFFFFF;
            guiText = num(p, "guiText", 0xF4F7FA) & 0xFFFFFF;
            zoomFov = Math.max(30, Math.min(90, num(p, "zoomFov", 30)));
            npEnabled = b(p, "npEnabled", false);
            npName = p.getProperty("npName", "Krux");
            infoEnabled = b(p, "infoEnabled", false);
            infoCoords = b(p, "infoCoords", true);
            infoFps = b(p, "infoFps", true);
            infoDir = b(p, "infoDir", true);
            favorites.clear();
            for (String fav : p.getProperty("favorites", "").split(",")) {
                if (!fav.isBlank()) favorites.add(fav.trim());
            }

            freecamSpeed = dbl(p, "freecamSpeed", 3.0);
            auraRange = dbl(p, "auraRange", 3.0);
            auraSpeed = dbl(p, "auraSpeed", 8.0);
            auraPlayersOnly = b(p, "auraPlayersOnly", true);
            auraCooldown = b(p, "auraCooldown", true);
            auraWalls = b(p, "auraWalls", false);
            auraRotate = b(p, "auraRotate", false);
            auraHitMode = Math.max(0, Math.min(3, num(p, "auraHitMode", 3)));
            auraPriority = Math.max(0, Math.min(2, num(p, "auraPriority", 0)));
            auraMinCharge = Math.max(50, Math.min(100, num(p, "auraMinCharge", 91)));
            auraCritSens = Math.max(1.0, Math.min(5.0, dbl(p, "auraCritSens", 2.0)));
            auraStopSprint = b(p, "auraStopSprint", true);
            auraSilent = b(p, "auraSilent", true);
            auraRandomDelay = b(p, "auraRandomDelay", false);
            auraRotSpeed = dbl(p, "auraRotSpeed", 40.0);
            auraJitter = dbl(p, "auraJitter", 0.15);
            auraSeek = Math.max(3.0, Math.min(6.0, dbl(p, "auraSeek", 4.2)));
            auraMinCps = Math.max(1.0, Math.min(20.0, dbl(p, "auraMinCps", 9.0)));
            auraMaxCps = Math.max(1.0, Math.min(20.0, dbl(p, "auraMaxCps", 13.0)));
            auraRotMode = Math.max(0, Math.min(3, num(p, "auraRotMode", 0)));
            auraPredict = b(p, "auraPredict", true);
            auraMinRot = Math.max(0.0, Math.min(10.0, dbl(p, "auraMinRot", 3.0)));
            auraMaxRot = Math.max(0.0, Math.min(10.0, dbl(p, "auraMaxRot", 7.0)));
            auraRaycast = b(p, "auraRaycast", true);
            auraMissRate = Math.max(0.0, Math.min(20.0, dbl(p, "auraMissRate", 0.0)));
            auraKeepSprint = b(p, "auraKeepSprint", false);
            auraTeams = b(p, "auraTeams", false);
            armorMode = Math.max(0, Math.min(1, num(p, "armorMode", 0)));
            armorStartDelay = Math.max(0, Math.min(1000, num(p, "armorStartDelay", 150)));
            armorSpeed = Math.max(0, Math.min(1000, num(p, "armorSpeed", 150)));
            autoToolSneak = b(p, "autoToolSneak", false);
            autoToolSword = b(p, "autoToolSword", true);
        } catch (Exception ignored) {
        }
        lastSig = signature();
    }

    private static int signature() {
        return Objects.hash(enabled, Arrays.hashCode(types), radiusChunks, susEnabled, susRange);
    }

    public static void save() {
        int sig = signature();
        if (sig != lastSig) {
            lastSig = sig;
            StorageScanner.requestRescan();   // tylko gdy zmieniło się coś ważnego dla skanera
        }
        try {
            Properties p = new Properties();
            p.setProperty("enabled", Boolean.toString(enabled));
            for (var e : XN.entrySet()) p.setProperty("x." + e.getKey(), Double.toString(e.getValue()));
            for (StorageType t : StorageType.values()) {
                p.setProperty("type." + t.name(), Boolean.toString(types[t.ordinal()]));
            }
            p.setProperty("tracers", Boolean.toString(tracers));
            p.setProperty("radius", Integer.toString(radiusChunks));
            p.setProperty("susEnabled", Boolean.toString(susEnabled));
            p.setProperty("susRange", Integer.toString(susRange));
            p.setProperty("susTracers", Boolean.toString(susTracers));
            p.setProperty("susThr", Integer.toString(susThreshold));
            p.setProperty("susNotify", Boolean.toString(susNotify));

            p.setProperty("espEnabled", Boolean.toString(espEnabled));
            p.setProperty("espPlayers", Boolean.toString(espPlayers));
            p.setProperty("espHostile", Boolean.toString(espHostile));
            p.setProperty("espPassive", Boolean.toString(espPassive));
            p.setProperty("espPlayerColor", Integer.toString(espPlayerColor));
            p.setProperty("espMobColor", Integer.toString(espMobColor));
            p.setProperty("espOpacity", Integer.toString(espOpacity));

            p.setProperty("tracersEnabled", Boolean.toString(tracersEnabled));
            p.setProperty("trPlayers", Boolean.toString(trPlayers));
            p.setProperty("trHostile", Boolean.toString(trHostile));
            p.setProperty("trPassive", Boolean.toString(trPassive));
            p.setProperty("tracerThickness", Double.toString(tracerThickness));

            p.setProperty("arrayList", Boolean.toString(arrayList));
            p.setProperty("alBackground", Boolean.toString(alBackground));
            p.setProperty("alSuffix", Boolean.toString(alSuffix));
            p.setProperty("alColorMode", Integer.toString(alColorMode));
            p.setProperty("alSort", Integer.toString(alSort));
            p.setProperty("accent", Integer.toString(accent));
            p.setProperty("auraEffect", Integer.toString(auraEffect));
            p.setProperty("auraEffectColor", Integer.toString(auraEffectColor));
            p.setProperty("targetHud", Boolean.toString(targetHud));
            p.setProperty("targetHudX", Integer.toString(targetHudX));
            p.setProperty("targetHudY", Integer.toString(targetHudY));
            p.setProperty("fogEnabled", Boolean.toString(fogEnabled));
            p.setProperty("fogStart", Integer.toString(fogStart));
            p.setProperty("fogEnd", Integer.toString(fogEnd));
            p.setProperty("fogColor", Integer.toString(fogColor));
            p.setProperty("alAccent", Integer.toString(alAccent));
            p.setProperty("guiSize", Integer.toString(guiSize));
            p.setProperty("guiWidth", Integer.toString(guiWidth));
            p.setProperty("guiHeight", Integer.toString(guiHeight));
            p.setProperty("guiRound", Integer.toString(guiRound));
            p.setProperty("guiBg", Integer.toString(guiBg));
            p.setProperty("guiText", Integer.toString(guiText));
            p.setProperty("zoomFov", Integer.toString(zoomFov));
            p.setProperty("npEnabled", Boolean.toString(npEnabled));
            p.setProperty("npName", npName == null ? "Krux" : npName);
            p.setProperty("infoEnabled", Boolean.toString(infoEnabled));
            p.setProperty("infoCoords", Boolean.toString(infoCoords));
            p.setProperty("infoFps", Boolean.toString(infoFps));
            p.setProperty("infoDir", Boolean.toString(infoDir));
            p.setProperty("favorites", String.join(",", favorites));

            p.setProperty("freecamSpeed", Double.toString(freecamSpeed));
            p.setProperty("auraRange", Double.toString(auraRange));
            p.setProperty("auraSpeed", Double.toString(auraSpeed));
            p.setProperty("auraPlayersOnly", Boolean.toString(auraPlayersOnly));
            p.setProperty("auraCooldown", Boolean.toString(auraCooldown));
            p.setProperty("auraWalls", Boolean.toString(auraWalls));
            p.setProperty("auraRotate", Boolean.toString(auraRotate));
            p.setProperty("auraHitMode", Integer.toString(auraHitMode));
            p.setProperty("auraPriority", Integer.toString(auraPriority));
            p.setProperty("auraMinCharge", Integer.toString(auraMinCharge));
            p.setProperty("auraCritSens", Double.toString(auraCritSens));
            p.setProperty("auraStopSprint", Boolean.toString(auraStopSprint));
            p.setProperty("auraSilent", Boolean.toString(auraSilent));
            p.setProperty("auraRandomDelay", Boolean.toString(auraRandomDelay));
            p.setProperty("auraRotSpeed", Double.toString(auraRotSpeed));
            p.setProperty("auraJitter", Double.toString(auraJitter));
            p.setProperty("auraSeek", Double.toString(auraSeek));
            p.setProperty("auraMinCps", Double.toString(auraMinCps));
            p.setProperty("auraMaxCps", Double.toString(auraMaxCps));
            p.setProperty("auraRotMode", Integer.toString(auraRotMode));
            p.setProperty("auraPredict", Boolean.toString(auraPredict));
            p.setProperty("auraMinRot", Double.toString(auraMinRot));
            p.setProperty("auraMaxRot", Double.toString(auraMaxRot));
            p.setProperty("auraRaycast", Boolean.toString(auraRaycast));
            p.setProperty("auraMissRate", Double.toString(auraMissRate));
            p.setProperty("auraKeepSprint", Boolean.toString(auraKeepSprint));
            p.setProperty("auraTeams", Boolean.toString(auraTeams));
            p.setProperty("armorMode", Integer.toString(armorMode));
            p.setProperty("armorStartDelay", Integer.toString(armorStartDelay));
            p.setProperty("armorSpeed", Integer.toString(armorSpeed));
            p.setProperty("autoToolSneak", Boolean.toString(autoToolSneak));
            p.setProperty("autoToolSword", Boolean.toString(autoToolSword));
            try (OutputStream out = Files.newOutputStream(file())) {
                p.store(out, "Storage Finder");
            }
        } catch (Exception ignored) {
        }
    }
}
