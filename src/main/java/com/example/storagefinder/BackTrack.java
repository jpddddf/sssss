package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Back Track: opóźnia pakiety ruchu celu Aury (gracza), dzięki czemu widzisz go chwilę "w tyle"
 * (patrz mixin/ConnectionReadMixin). Tryby: Constant / Hit / Zero (wg hurtTime celu).
 */
public final class BackTrack {
    private record Held(Packet<?> packet, long due) {}

    private static final ConcurrentLinkedQueue<Held> queue = new ConcurrentLinkedQueue<>();
    private static volatile int targetId = -1;
    private static volatile boolean delaying = false;
    private static volatile long delayMs = 100L;

    private BackTrack() {}

    static boolean on() { return StorageFinderConfig.xb("m.backtrack", false) && Krux.licensed(); }

    private static int entityId(Packet<?> p) {
        if (p instanceof ClientboundMoveEntityPacket m) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return -1;
            Entity e = m.getEntity(mc.level);
            return e == null ? -1 : e.getId();
        }
        if (p instanceof ClientboundTeleportEntityPacket t) return t.id();
        if (p instanceof ClientboundEntityPositionSyncPacket s) return s.id();
        return -1;
    }

    /** Wołane z wątku sieciowego (mixin): true = pakiet zatrzymany w kolejce. */
    public static boolean intercept(Packet<?> p) {
        int tid = targetId;
        if (tid < 0 || !on()) return false;
        if (!delaying && queue.isEmpty()) return false;
        if (entityId(p) != tid) return false;
        queue.add(new Held(p, System.currentTimeMillis() + (delaying ? delayMs : 0L)));
        return true;
    }

    @SuppressWarnings("unchecked")
    private static void handle(Minecraft mc, Packet<?> p) {
        if (mc.getConnection() == null) return;
        try {
            ((Packet<ClientGamePacketListener>) p).handle(mc.getConnection());
        } catch (Exception ignored) {
        }
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null) {
            queue.clear();
            targetId = -1;
            delaying = false;
            return;
        }

        Entity t = on() ? Aura.targetEspEntity() : null;
        if (t instanceof Player tp) {
            double dist = p.distanceTo(tp);
            int mode = StorageFinderConfig.xi("bt.mode", 0);
            boolean modeOk = mode == 0 || (mode == 1 && tp.hurtTime != 0) || (mode == 2 && tp.hurtTime == 0);
            boolean damageOk = !StorageFinderConfig.xb("bt.release", true) || p.hurtTime == 0;
            boolean active = modeOk && damageOk
                    && dist >= StorageFinderConfig.xd("bt.near", 0.0) && dist <= StorageFinderConfig.xd("bt.far", 10.0);
            double lo = StorageFinderConfig.xd("bt.min", 50), hi = StorageFinderConfig.xd("bt.max", 200);
            if (lo > hi) { double x = lo; lo = hi; hi = x; }
            delayMs = (long) (lo + Math.random() * (hi - lo));
            targetId = tp.getId();
            delaying = active;
        } else {
            delaying = false;
            if (queue.isEmpty()) targetId = -1;
        }

        long now = System.currentTimeMillis();
        Held h;
        while ((h = queue.peek()) != null && (!delaying || h.due() <= now)) {
            queue.poll();
            handle(mc, h.packet());
        }
    }
}
