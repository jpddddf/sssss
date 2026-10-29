package com.example.storagefinder;

import java.util.ArrayList;
import java.util.List;

/** Rejestr modułów widocznych w Click GUI. */
public final class Modules {
    private static final int NONE = 0;

    // pomocnicy dla nowych modułów (ustawienia trzymane w StorageFinderConfig.XN)
    private static Setting sl(String name, double min, double max, double step, boolean integer, String key, double def) {
        return new Setting.Slider(name, min, max, step, integer,
                () -> StorageFinderConfig.xd(key, def), v -> StorageFinderConfig.xset(key, v));
    }

    private static Setting bo(String name, String key, boolean def) {
        return new Setting.Bool(name, NONE, () -> StorageFinderConfig.xb(key, def), v -> StorageFinderConfig.xsetb(key, v));
    }

    private static Setting mo(String name, String[] opts, String key, int def) {
        return new Setting.Mode(name, opts, () -> StorageFinderConfig.xi(key, def), v -> StorageFinderConfig.xset(key, v));
    }

    private static Setting sw(String name, String key, int def) {
        return new Setting.Swatches(name, GUI_ACCENT_NAMES, GUI_ACCENT_COLORS,
                () -> StorageFinderConfig.xi(key, def), v -> StorageFinderConfig.xset(key, v));
    }

    private static Module mod(String name, String cat, String desc, String[] tags, String key, boolean def, Setting... s) {
        return new Module(name, cat, desc, tags, () -> StorageFinderConfig.xb(key, def),
                v -> StorageFinderConfig.xsetb(key, v), List.of(s));
    }
                 // brak kolorowej kropki w GUI
    private static final int PLAYER = 0xFF3DD6FF;
    private static final int MOB = 0xFFFF5555;
    private static final int PASSIVE = 0xFF58D68D;

    public static final String[] ACCENT_NAMES = {"Ice", "Crimson", "Emerald", "Violet", "Gold", "Diamond"};
    public static final String[] GUI_ACCENT_NAMES = {"Ice", "Crimson", "Emerald", "Violet", "Gold", "Diamond",
            "Pink", "Orange", "Lime", "Magenta", "Blue", "White"};
    public static final int[] GUI_ACCENT_COLORS = {0x4FB3E8, 0xE5484D, 0x34C985, 0x9D86EA, 0xFFC73A, 0x45E0E6,
            0xF08CA0, 0xFF8A3D, 0xA6E22E, 0xE040C8, 0x4C6FFF, 0xE6EAF0};
    public static final String[] GUI_BG_NAMES = {"Default", "Midnight", "Black", "Navy", "Ocean", "Indigo", "Violet", "Plum",
            "Wine", "Crimson", "Rose", "Brown", "Olive", "Forest", "Emerald", "Teal"};
    public static final int[] GUI_BG_COLORS = {0x15181C, 0x0F1218, 0x0A0A0C, 0x121A2B, 0x0D2A4A, 0x1E1B4B, 0x2E1A5C, 0x3A1450,
            0x4A1222, 0x3D0F14, 0x4A1B33, 0x3A2412, 0x2A3012, 0x123A22, 0x0F3D33, 0x0F3A44};
    public static final String[] GUI_TEXT_NAMES = {"Default", "White", "Ice", "Sky", "Blue", "Violet", "Pink", "Rose",
            "Red", "Orange", "Gold", "Lime", "Green", "Mint", "Cyan", "Gray"};
    public static final int[] GUI_TEXT_COLORS = {0xF4F7FA, 0xFFFFFF, 0x9FE0FF, 0x7CC4FF, 0x8FA8FF, 0xC3A6FF, 0xFF9EDC, 0xFF8FA3,
            0xFF7A7A, 0xFFB366, 0xFFD966, 0xD4F26B, 0x7DF0A5, 0x8FFFE0, 0x66F0FF, 0xC8CED6};
    public static final String[] EFFECT_NAMES = {"None", "Ring", "Box", "Halo", "Orbit", "Helix"};
    public static final String[] FOG_NAMES = {"Mist", "Night", "Blood", "Violet", "Ice", "Toxic",
            "Gold", "Pink", "Dark", "Sunset", "Teal", "White"};
    public static final int[] FOG_COLORS = {0xB8C4D0, 0x0B0F1A, 0x8A1C1C, 0x6E4BC8, 0x7FC8F0, 0x6FE06F,
            0xE8C060, 0xF08CB0, 0x1A1A1A, 0xFF8A5C, 0x2FA8A0, 0xF2F4F8};
    public static final int[] ACCENT_COLORS = {0x4FB3E8, 0xE5484D, 0x34C985, 0x9D86EA, 0xFFC73A, 0x45E0E6};

    public static final List<String> CATEGORIES = List.of("Combat", "Movement", "Misc", "Render");
    public static final List<Module> ALL = new ArrayList<>();
    public static final Module ARRAY_LIST;

    private Modules() {}

    private static String f1(double v) {
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }

    static {
        // ---------------- Combat ----------------
        ALL.add(new Module("Aura", "Combat",
                "Attacks the closest target in range on its own.",
                new String[]{"Aim", "Legit"},
                Aura::isEnabled, Aura::setEnabled, List.of(
                new Setting.Slider("Range", 3, 6, 0.1, false,
                        () -> StorageFinderConfig.auraRange, v -> StorageFinderConfig.auraRange = v),
                new Setting.Mode("Rotations", new String[]{"Regular", "Polar", "Snap", "None"},
                        () -> StorageFinderConfig.auraRotMode, v -> StorageFinderConfig.auraRotMode = v),
                new Setting.Slider("Miss chance %", 0, 20, 1, true,
                        () -> StorageFinderConfig.auraMissRate, v -> StorageFinderConfig.auraMissRate = v),
                new Setting.Mode("Effects", EFFECT_NAMES,
                        () -> StorageFinderConfig.auraEffect, v -> StorageFinderConfig.auraEffect = v),
                new Setting.Swatches("Effect color", GUI_ACCENT_NAMES, GUI_ACCENT_COLORS,
                        () -> StorageFinderConfig.auraEffectColor, v -> StorageFinderConfig.auraEffectColor = v)
        )).withSuffix(() -> StorageFinderConfig.auraPlayersOnly ? "Players" : "All"));

        // ---------------- Movement ----------------
        ALL.add(new Module("Sprint", "Movement",
                "Sprints automatically while you walk forward.",
                new String[]{"Basics", "Utility"},
                Sprint::isEnabled, Sprint::setEnabled, List.of()));

        ALL.add(new Module("Freecam", "Movement",
                "Fly the camera freely while your character stays in place.",
                new String[]{"Camera", "Utility"},
                Freecam::isActive, Freecam::setActive, List.of(
                new Setting.Slider("Speed", 1, 10, 0.5, false,
                        () -> StorageFinderConfig.freecamSpeed, v -> StorageFinderConfig.freecamSpeed = v)
        )).withSuffix(() -> f1(StorageFinderConfig.freecamSpeed)));

        // ---------------- Render ----------------
        List<Setting> sf = new ArrayList<>();
        for (StorageType t : StorageType.values()) {
            sf.add(new Setting.Bool(t.label, t.argb(),
                    () -> StorageFinderConfig.isOn(t), v -> StorageFinderConfig.setOn(t, v)));
        }
        sf.add(new Setting.Bool("Tracers", NONE,
                () -> StorageFinderConfig.tracers, v -> StorageFinderConfig.tracers = v));
        sf.add(new Setting.Slider("Scan range", 2, 16, 1, true,
                () -> StorageFinderConfig.radiusChunks, v -> StorageFinderConfig.radiusChunks = (int) Math.round(v)));

        ALL.add(new Module("Storage Finder", "Render",
                "Finds chests, barrels, spawners, hoppers, pistons and amethyst through walls.",
                new String[]{"Search", "Blocks"},
                () -> StorageFinderConfig.enabled, v -> StorageFinderConfig.enabled = v, sf)
                .withSuffix(() -> String.valueOf(StorageScanner.found().size())));

        ALL.add(new Module("Sus Chunk", "Render",
                "Marks a chunk in red when it holds a huge amount of fully grown amethyst.",
                new String[]{"Chunks", "Amethyst"},
                () -> StorageFinderConfig.susEnabled, v -> StorageFinderConfig.susEnabled = v, List.of(
                new Setting.Slider("Threshold", 0, 500, 1, true,
                        () -> StorageFinderConfig.susThreshold, v -> StorageFinderConfig.susThreshold = (int) Math.round(v)),
                new Setting.Slider("Scan range", 2, 16, 1, true,
                        () -> StorageFinderConfig.susRange, v -> StorageFinderConfig.susRange = (int) Math.round(v)),
                new Setting.Bool("Tracers", NONE,
                        () -> StorageFinderConfig.susTracers, v -> StorageFinderConfig.susTracers = v),
                new Setting.Bool("Chat alert", NONE,
                        () -> StorageFinderConfig.susNotify, v -> StorageFinderConfig.susNotify = v)
        )).withSuffix(() -> String.valueOf(StorageScanner.sus().size())));

        ALL.add(new Module("ESP", "Render",
                "Boxes around players and mobs, visible through walls.",
                new String[]{"Entities", "Visual"},
                () -> StorageFinderConfig.espEnabled, v -> StorageFinderConfig.espEnabled = v, List.of(
                new Setting.Bool("Players", PLAYER,
                        () -> StorageFinderConfig.espPlayers, v -> StorageFinderConfig.espPlayers = v),
                new Setting.Bool("Hostile mobs", MOB,
                        () -> StorageFinderConfig.espHostile, v -> StorageFinderConfig.espHostile = v),
                new Setting.Bool("Passive mobs", PASSIVE,
                        () -> StorageFinderConfig.espPassive, v -> StorageFinderConfig.espPassive = v),
                new Setting.ColorPick("Player color",
                        () -> StorageFinderConfig.espPlayerColor, v -> StorageFinderConfig.espPlayerColor = v),
                new Setting.ColorPick("Mob color",
                        () -> StorageFinderConfig.espMobColor, v -> StorageFinderConfig.espMobColor = v),
                new Setting.Slider("Opacity", 10, 100, 5, true,
                        () -> StorageFinderConfig.espOpacity, v -> StorageFinderConfig.espOpacity = (int) Math.round(v))
        )).withSuffix(() -> StorageFinderConfig.espPlayers ? "Players" : "Mobs"));

        ALL.add(new Module("Tracers", "Render",
                "Lines from your crosshair to players and mobs. Uses the ESP colors.",
                new String[]{"Lines", "Visual"},
                () -> StorageFinderConfig.tracersEnabled, v -> StorageFinderConfig.tracersEnabled = v, List.of(
                new Setting.Bool("Players", PLAYER,
                        () -> StorageFinderConfig.trPlayers, v -> StorageFinderConfig.trPlayers = v),
                new Setting.Bool("Hostile mobs", MOB,
                        () -> StorageFinderConfig.trHostile, v -> StorageFinderConfig.trHostile = v),
                new Setting.Bool("Passive mobs", PASSIVE,
                        () -> StorageFinderConfig.trPassive, v -> StorageFinderConfig.trPassive = v),
                new Setting.Slider("Thickness", 0.5, 4, 0.5, false,
                        () -> StorageFinderConfig.tracerThickness, v -> StorageFinderConfig.tracerThickness = v)
        )));

        ALL.add(new Module("Fullbright", "Render",
                "Full brightness everywhere, even in caves and at night.",
                new String[]{"Light"},
                Fullbright::isEnabled, Fullbright::setEnabled, List.of()));

        ALL.add(new Module("Zoom", "Render",
                "Zooms the view in. The previous FOV is restored when you turn it off.",
                new String[]{"FOV"},
                Zoom::isEnabled, Zoom::setEnabled, List.of(
                new Setting.Slider("Zoom FOV", 30, 90, 1, true,
                        () -> StorageFinderConfig.zoomFov, v -> StorageFinderConfig.zoomFov = (int) Math.round(v))
        )));

        ALL.add(new Module("Target ESP", "Render",
                "Name, HP and armor of your Aura target. Right-click in chat to toggle, drag it in chat to move.",
                new String[]{"Aura", "HUD"},
                () -> StorageFinderConfig.targetHud, v -> StorageFinderConfig.targetHud = v, List.of()));

        ALL.add(new Module("Fog", "Render",
                "Custom fog: color and distance.",
                new String[]{"Visual", "World"},
                () -> StorageFinderConfig.fogEnabled, v -> StorageFinderConfig.fogEnabled = v, List.of(
                new Setting.Slider("Start", 0, 150, 5, true,
                        () -> StorageFinderConfig.fogStart, v -> StorageFinderConfig.fogStart = (int) Math.round(v)),
                new Setting.Slider("End", 10, 300, 5, true,
                        () -> StorageFinderConfig.fogEnd, v -> StorageFinderConfig.fogEnd = (int) Math.round(v)),
                new Setting.Swatches("Color", FOG_NAMES, FOG_COLORS,
                        () -> StorageFinderConfig.fogColor, v -> StorageFinderConfig.fogColor = v)
        )));

        // ---------------- Misc ----------------
        ALL.add(mod("Aim Assist", "Combat", "Gently pulls your crosshair toward the nearest target while you attack.",
                new String[]{"Aim", "Assist"}, "m.aimassist", false,
                sl("Search range", 1, 8, 0.1, false, "aa.range", 4.0),
                bo("Only on click", "aa.click", true),
                sl("Reset time", 0, 1000, 10, true, "aa.reset", 500),
                bo("Team check", "aa.team", false),
                sl("Horizontal speed", 0.1, 10, 0.1, false, "aa.hspeed", 3.5),
                sl("Vertical speed", 0.1, 10, 0.1, false, "aa.vspeed", 3.0),
                sl("Max angle", 10, 180, 1, true, "aa.angle", 90),
                bo("Smoothing", "aa.smooth", true)));

        ALL.add(mod("Back Track", "Combat", "Delays the movement of your Aura target so you see them a bit behind.",
                new String[]{"Lag", "Combat"}, "m.backtrack", false,
                mo("Mode", new String[]{"Constant", "Hit", "Zero"}, "bt.mode", 0),
                sl("Min delay", 0, 1000, 10, true, "bt.min", 50),
                sl("Max delay", 0, 1000, 10, true, "bt.max", 200),
                sl("Activate distance", 0, 10, 0.1, false, "bt.near", 0.0),
                sl("Deactivate distance", 0, 10, 0.1, false, "bt.far", 10.0),
                bo("Release on damage", "bt.release", true)));

        ALL.add(mod("Criticals", "Combat", "Makes your hits critical: particles, a jump after the hit, or position packets.",
                new String[]{"Crit", "Combat"}, "m.criticals", false,
                mo("Mode", new String[]{"Visual", "Legit", "NCP"}, "cr.mode", 2)));

        ALL.add(mod("Reach", "Combat", "Extends your attack range (random between Min and Max).",
                new String[]{"Range", "Combat"}, "m.reach", false,
                sl("Min range", 3, 6, 0.01, false, "re.min", 3.0),
                sl("Max range", 3, 6, 0.01, false, "re.max", 3.0)));

        ALL.add(mod("Hitboxes", "Combat", "Expands the hitbox of other entities when you aim.",
                new String[]{"Hitbox", "Combat"}, "m.hitboxes", false,
                sl("Expand amount", 0, 1, 0.01, false, "hb.expand", 0.0)));

        ALL.add(mod("Blink", "Combat", "Holds your movement packets and releases them later.",
                new String[]{"Packets", "Combat"}, "m.blink", false,
                mo("Mode", new String[]{"Constant", "Pulse"}, "bl.mode", 0),
                sl("Delay", 0, 100, 1, true, "bl.delay", 20),
                bo("Show blink position", "bl.box", true)));

        ALL.add(mod("China Hat", "Render", "A cone hat above your head.",
                new String[]{"Cosmetic", "Visual"}, "m.chinahat", false,
                sl("Radius", 0.3, 1.5, 0.05, false, "ch.radius", 0.65),
                sl("Height", 0.2, 1.5, 0.05, false, "ch.height", 0.5),
                bo("Show in first person", "ch.first", false),
                sw("Color", "ch.color", 0x4FB3E8)));

        ALL.add(mod("Jump Circle", "Render", "A ring that spreads out where you land after a jump.",
                new String[]{"Cosmetic", "Visual"}, "m.jumpcircle", false,
                sl("Max radius", 1, 4, 0.1, false, "jc.radius", 2.0),
                sl("Duration", 300, 2000, 50, true, "jc.time", 900),
                sw("Color", "jc.color", 0x4FB3E8)));

        ALL.add(mod("Name Tags", "Render", "Name, HP and armor above players, visible through walls.",
                new String[]{"Players", "Visual"}, "m.nametags", false,
                bo("Show health", "nt.hp", true),
                bo("Show armor", "nt.armor", true),
                bo("Show distance", "nt.dist", false),
                bo("Background", "nt.bg", true),
                sl("Scale", 0.5, 2, 0.1, false, "nt.scale", 1.0)));

        ALL.add(mod("Notifications", "Render", "Shows a pop-up when you toggle a module.",
                new String[]{"HUD", "Visual"}, "m.notifications", true));

        ALL.add(new Module("GUI", "Misc",
                "Style of this menu: size, shape and colors.",
                new String[]{"Style", "Theme"},
                () -> true, v -> {}, List.of(
                new Setting.Slider("Size %", 60, 130, 5, true,
                        () -> StorageFinderConfig.guiSize, v -> StorageFinderConfig.guiSize = (int) Math.round(v)),
                new Setting.Slider("Width %", 70, 120, 5, true,
                        () -> StorageFinderConfig.guiWidth, v -> StorageFinderConfig.guiWidth = (int) Math.round(v)),
                new Setting.Slider("Height %", 70, 120, 5, true,
                        () -> StorageFinderConfig.guiHeight, v -> StorageFinderConfig.guiHeight = (int) Math.round(v)),
                new Setting.Slider("Rounding", 0, 40, 2, true,
                        () -> StorageFinderConfig.guiRound, v -> StorageFinderConfig.guiRound = (int) Math.round(v)),
                new Setting.Swatches("Background", GUI_BG_NAMES, GUI_BG_COLORS,
                        () -> StorageFinderConfig.guiBg, v -> StorageFinderConfig.guiBg = v),
                new Setting.Swatches("Accent", GUI_ACCENT_NAMES, GUI_ACCENT_COLORS,
                        () -> StorageFinderConfig.accent, v -> StorageFinderConfig.accent = v),
                new Setting.Swatches("Text", GUI_TEXT_NAMES, GUI_TEXT_COLORS,
                        () -> StorageFinderConfig.guiText, v -> StorageFinderConfig.guiText = v)
        )));

        ARRAY_LIST = new Module("ArrayList", "Misc",
                "Lists enabled modules in the top right corner. Has its own accent color.",
                new String[]{"HUD"},
                () -> StorageFinderConfig.arrayList, v -> StorageFinderConfig.arrayList = v, List.of(
                new Setting.Swatches("Accent", ACCENT_NAMES, ACCENT_COLORS,
                        () -> StorageFinderConfig.alAccent, v -> StorageFinderConfig.alAccent = v),
                new Setting.Mode("Color mode", new String[]{"Static", "Rainbow", "Gradient"},
                        () -> StorageFinderConfig.alColorMode, v -> StorageFinderConfig.alColorMode = v),
                new Setting.Mode("Sort", new String[]{"Length", "A-Z"},
                        () -> StorageFinderConfig.alSort, v -> StorageFinderConfig.alSort = v),
                new Setting.Bool("Suffixes", NONE,
                        () -> StorageFinderConfig.alSuffix, v -> StorageFinderConfig.alSuffix = v),
                new Setting.Bool("Background", NONE,
                        () -> StorageFinderConfig.alBackground, v -> StorageFinderConfig.alBackground = v)
        ));
        ALL.add(ARRAY_LIST);

        ALL.add(new Module("Name Protect", "Misc",
                "Replaces your real name with a fake one in chat, tab list and nametags (client side only).",
                new String[]{"Privacy"},
                () -> StorageFinderConfig.npEnabled, v -> StorageFinderConfig.npEnabled = v, List.of(
                new Setting.Text("Fake name", 16,
                        () -> StorageFinderConfig.npName, v -> StorageFinderConfig.npName = v)
        )));

        ALL.add(new Module("Auto Armor", "Misc",
                "Automatically wears the best armor from your inventory.",
                new String[]{"Inventory", "Utility"},
                AutoArmor::isEnabled, AutoArmor::setEnabled, List.of(
                new Setting.Mode("Mode", new String[]{"Open", "Spoof"},
                        () -> StorageFinderConfig.armorMode, v -> StorageFinderConfig.armorMode = v),
                new Setting.Slider("Start delay", 0, 1000, 10, true,
                        () -> StorageFinderConfig.armorStartDelay, v -> StorageFinderConfig.armorStartDelay = (int) Math.round(v)),
                new Setting.Slider("Speed", 0, 1000, 10, true,
                        () -> StorageFinderConfig.armorSpeed, v -> StorageFinderConfig.armorSpeed = (int) Math.round(v))
        )).withSuffix(() -> StorageFinderConfig.armorMode == 0 ? "Open" : "Spoof"));

        ALL.add(new Module("Auto Tool", "Misc",
                "Picks the best hotbar tool for the block, and the best sword for attacks.",
                new String[]{"Hotbar", "Utility"},
                AutoTool::isEnabled, AutoTool::setEnabled, List.of(
                new Setting.Bool("Sneak only", NONE,
                        () -> StorageFinderConfig.autoToolSneak, v -> StorageFinderConfig.autoToolSneak = v),
                new Setting.Bool("Swap to sword", NONE,
                        () -> StorageFinderConfig.autoToolSword, v -> StorageFinderConfig.autoToolSword = v)
        )));

        ALL.add(new Module("Info", "Misc",
                "Shows coordinates, FPS and facing direction in the top left corner.",
                new String[]{"HUD", "Info"},
                () -> StorageFinderConfig.infoEnabled, v -> StorageFinderConfig.infoEnabled = v, List.of(
                new Setting.Bool("Coordinates", NONE,
                        () -> StorageFinderConfig.infoCoords, v -> StorageFinderConfig.infoCoords = v),
                new Setting.Bool("FPS", NONE,
                        () -> StorageFinderConfig.infoFps, v -> StorageFinderConfig.infoFps = v),
                new Setting.Bool("Direction", NONE,
                        () -> StorageFinderConfig.infoDir, v -> StorageFinderConfig.infoDir = v)
        )));
    }
}
