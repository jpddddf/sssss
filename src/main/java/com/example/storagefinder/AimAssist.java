package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Aim Assist (port z Simp, tryb Player): delikatnie dociąga celownik do najbliższego celu przy klikaniu. */
public final class AimAssist {
    private static long lastClick = 0L;
    private static float smoothYaw = 0f, smoothPitch = 0f;

    private AimAssist() {}

    static boolean on() { return StorageFinderConfig.xb("m.aimassist", false) && Krux.licensed(); }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (!on() || p == null || mc.level == null || mc.screen != null || Freecam.isActive()) {
            smoothYaw = smoothPitch = 0f;
            return;
        }
        long now = System.currentTimeMillis();
        if (StorageFinderConfig.xb("aa.click", true)) {
            if (mc.options.keyAttack.isDown()) lastClick = now;
            if (now - lastClick > StorageFinderConfig.xd("aa.reset", 500)) {
                smoothYaw = smoothPitch = 0f;
                return;
            }
        }

        double range = StorageFinderConfig.xd("aa.range", 4.0);
        Vec3 eye = p.getEyePosition();
        float bestY = 0, bestP = 0;
        double bestAng = Double.MAX_VALUE;
        for (LivingEntity e : mc.level.getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(range + 1.0))) {
            if (e == p || !e.isAlive() || e.isSpectator()) continue;
            if (!(e instanceof Player || e instanceof Enemy)) continue;
            if (e instanceof Player pl && pl.isCreative()) continue;
            if (StorageFinderConfig.xb("aa.team", false) && p.isAlliedTo(e)) continue;
            if (e.getBoundingBox().distanceToSqr(eye) > range * range) continue;
            if (!p.hasLineOfSight(e)) continue;
            double dx = e.getX() - eye.x, dy = e.getEyeY() - eye.y, dz = e.getZ() - eye.z;
            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
            float yd = Mth.wrapDegrees(yaw - p.getYRot()), pd = pitch - p.getXRot();
            double ang = Math.sqrt(yd * yd + pd * pd);
            if (ang < bestAng) {
                bestAng = ang;
                bestY = yd;
                bestP = pd;
            }
        }
        if (bestAng > StorageFinderConfig.xd("aa.angle", 90)) return;

        float f = StorageFinderConfig.xb("aa.smooth", true) ? 0.6f : 0f;
        smoothYaw = smoothYaw * f + bestY * (1f - f);
        smoothPitch = smoothPitch * f + bestP * (1f - f);
        float hs = (float) StorageFinderConfig.xd("aa.hspeed", 3.5), vs = (float) StorageFinderConfig.xd("aa.vspeed", 3.0);
        float dy = Math.signum(smoothYaw) * Math.min(Math.abs(smoothYaw), hs);
        float dp = Math.signum(smoothPitch) * Math.min(Math.abs(smoothPitch), vs);
        p.setYRot(p.getYRot() + dy);
        p.setXRot(Mth.clamp(p.getXRot() + dp, -90f, 90f));
    }
}
