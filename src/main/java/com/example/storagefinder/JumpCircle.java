package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.ArrayList;
import java.util.List;

/** Jump Circle: rozszerzający się, zanikający pierścień w miejscu lądowania po skoku. */
public final class JumpCircle {
    public record Circle(double x, double y, double z, long t0) {}

    private static final List<Circle> circles = new ArrayList<>();
    private static boolean wasAir = false;

    private JumpCircle() {}

    static boolean on() { return StorageFinderConfig.xb("m.jumpcircle", false) && Krux.licensed(); }

    public static boolean active() { return !circles.isEmpty(); }

    public static List<Circle> list() { return circles; }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null) {
            circles.clear();
            wasAir = false;
            return;
        }
        long now = System.currentTimeMillis();
        double dur = StorageFinderConfig.xd("jc.time", 900);
        circles.removeIf(c -> now - c.t0() > dur);
        if (!on()) {
            circles.clear();
            wasAir = !p.onGround();
            return;
        }
        boolean ground = p.onGround();
        if (ground && wasAir) circles.add(new Circle(p.getX(), p.getY() + 0.02, p.getZ(), now));
        wasAir = !ground;
    }
}
