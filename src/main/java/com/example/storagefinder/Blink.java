package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Blink: wstrzymuje pakiety ruchu do serwera (patrz mixin/ConnectionSendMixin). Tryby: Constant, Pulse. */
public final class Blink {
    private static final List<Packet<?>> held = new ArrayList<>();
    private static volatile boolean flushing = false;
    private static boolean wasOn = false;
    private static long lastFlush = 0L;
    private static Vec3 pos = null;

    private Blink() {}

    static boolean on() { return StorageFinderConfig.xb("m.blink", false) && Krux.licensed(); }

    /** Wołane z mixina: true = pakiet zatrzymany. */
    public static boolean intercept(Packet<?> p) {
        if (flushing || !on()) return false;
        if (p instanceof ServerboundMovePlayerPacket) {
            synchronized (held) { held.add(p); }
            return true;
        }
        return false;
    }

    private static void flush(Minecraft mc) {
        List<Packet<?>> copy;
        synchronized (held) {
            copy = new ArrayList<>(held);
            held.clear();
        }
        if (mc.player == null || mc.player.connection == null) return;
        flushing = true;
        try {
            for (Packet<?> p : copy) mc.player.connection.send(p);
        } finally {
            flushing = false;
        }
    }

    public static void tick(Minecraft mc) {
        boolean on = on() && mc.player != null;
        if (mc.player == null) {
            synchronized (held) { held.clear(); }
            wasOn = false;
            pos = null;
            return;
        }
        long now = System.currentTimeMillis();
        if (on && !wasOn) {
            pos = mc.player.position();
            lastFlush = now;
        }
        if (on && StorageFinderConfig.xi("bl.mode", 0) == 1
                && now - lastFlush >= (long) (StorageFinderConfig.xd("bl.delay", 20) * 10)) {
            flush(mc);
            pos = mc.player.position();
            lastFlush = now;
        }
        if (!on && wasOn) {
            flush(mc);
            pos = null;
        }
        wasOn = on;
    }

    /** Pozycja widoczna dla serwera (do narysowania ramki w 3. osobie). */
    public static Vec3 renderPos(Minecraft mc) {
        if (!on() || pos == null || !StorageFinderConfig.xb("bl.box", true)) return null;
        return mc.options.getCameraType().isFirstPerson() ? null : pos;
    }
}
