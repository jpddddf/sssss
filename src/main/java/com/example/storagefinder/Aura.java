package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Aura — port Kill Aura (Simp) na Fabric 1.21.11.
 *
 * Z oryginału: Seek range / Kill range, Priority, Rotations (Regular/Polar/Snap/None), Predictive rotations,
 * min/max prędkość obrotu, Jitter, Ray cast, Miss rate, Keep sprint, Teams, opóźnienia 1.9+ (tryb 1.8/CPS usunięty).
 * Pominięte: AutoBlock (blokowanie mieczem istnieje tylko w 1.8), Move Fix, Switch mode.
 *
 * START ticka: wybiera cel (w Seek range), płynnie kręci "serwerowy" obrót w stronę celu (kamera tego nie widzi).
 * END ticka: jeśli cel w Kill range, timing OK i (opcjonalnie) promień trafia w cel — atakuje, potem przywraca obrót.
 */
public final class Aura {
    private static boolean enabled = false;

    private static LivingEntity pending = null;
    private static boolean rotated = false;
    private static float savedYaw, savedPitch, savedYawO, savedPitchO;

    // wirtualny obrót widziany przez serwer
    private static boolean targeting = false;
    private static LivingEntity lastTarget = null;
    private static long lastSeen = 0L;
    private static boolean hasAim = false;
    private static float aimYaw, aimPitch;

    private static final Random RNG = new Random();

    private Aura() {}

    public static boolean isEnabled() { return enabled; }

    /** Cel dla Target ESP (trzymany ~0.35 s po utracie, żeby efekt znikał płynnie). */
    public static LivingEntity targetEspEntity() {
        LivingEntity t = lastTarget;
        if (!enabled || t == null || !t.isAlive() || t.isRemoved()) return null;
        return System.currentTimeMillis() - lastSeen < 350L ? t : null;
    }

    public static float targetEspFade() {
        long d = System.currentTimeMillis() - lastSeen;
        return d < 100L ? 1f : Math.max(0f, 1f - (d - 100L) / 250f);
    }

    /** Czy Aura ma teraz cel (używane przez Auto Armor). */
    public static boolean hasTarget() { return enabled && targeting; }

    public static void setEnabled(boolean v) {
        enabled = v;
        pending = null;
        hasAim = false;
        lastTarget = null;
    }

    private static boolean ready(Minecraft mc) {
        LocalPlayer p = mc.player;
        return p != null && mc.level != null && mc.gameMode != null && mc.screen == null
                && p.isAlive() && !p.isUsingItem();
    }

    // ---------------------------------------------------------------- krytyki

    private static boolean critBase(LocalPlayer p) {
        return !p.onGround() && !p.onClimbable() && !p.isInWater()
                && !p.hasEffect(MobEffects.BLINDNESS) && !p.isPassenger() && !p.isSprinting();
    }

    private static boolean canCrit(LocalPlayer p) {
        return p.fallDistance > 0.0 && critBase(p);
    }

    private static boolean modeOkEnd(LocalPlayer p) {
        return switch (StorageFinderConfig.auraHitMode) {
            case 1 -> p.onGround();
            case 2 -> canCrit(p);
            case 3 -> p.onGround() || canCrit(p);
            default -> true;
        };
    }

    // ---------------------------------------------------------------- cele

    private static double killRange() { return StorageFinderConfig.auraRange; }

    private static double seekRange() { return Math.max(StorageFinderConfig.auraSeek, StorageFinderConfig.auraRange); }

    private static boolean validTarget(LocalPlayer p, LivingEntity e, Vec3 eye, double range) {
        if (e == p || !e.isAlive() || e.isSpectator() || !e.isAttackable()) return false;
        if (StorageFinderConfig.auraPlayersOnly && !(e instanceof Player)) return false;
        if (e instanceof Player pl && (pl.isCreative() || pl.isSpectator())) return false;
        if (StorageFinderConfig.auraTeams && p.isAlliedTo(e)) return false;
        if (e.getBoundingBox().distanceToSqr(eye) > range * range) return false;
        return StorageFinderConfig.auraWalls || p.hasLineOfSight(e);
    }

    private static LivingEntity findTarget(LocalPlayer p, ClientLevel level) {
        double range = seekRange();
        Vec3 eye = p.getEyePosition();
        int prio = StorageFinderConfig.auraPriority;
        Vec3 look = p.getLookAngle();

        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(range + 1.0))) {
            if (!validTarget(p, e, eye, range)) continue;
            double d2 = e.getBoundingBox().distanceToSqr(eye);
            double score;
            if (prio == 1) {
                score = e.getHealth() + d2 * 0.001;
            } else if (prio == 2) {
                Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
                double len = to.length();
                score = (len < 1e-6 ? 0.0 : 1.0 - to.dot(look) / len) + d2 * 1e-4;
            } else {
                score = d2;
            }
            if (score < bestScore) {
                best = e;
                bestScore = score;
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- obroty

    private static float[] aimAt(LocalPlayer p, LivingEntity t) {
        Vec3 eye = p.getEyePosition();
        AABB bb = t.getBoundingBox();
        double cx = (bb.minX + bb.maxX) * 0.5, cz = (bb.minZ + bb.maxZ) * 0.5;
        if (StorageFinderConfig.auraPredict) {           // przewidywanie ruchu celu
            cx += t.getX() - t.xOld;
            cz += t.getZ() - t.zOld;
        }
        double h = bb.getYsize();
        double ay = Mth.clamp(eye.y, bb.minY + h * 0.1, bb.maxY - h * 0.1);
        double j = Math.max(0.0, StorageFinderConfig.auraJitter);
        cx += (Math.random() * 2 - 1) * j * bb.getXsize() * 0.5;
        ay += (Math.random() * 2 - 1) * j * h * 0.5;
        cz += (Math.random() * 2 - 1) * j * bb.getZsize() * 0.5;

        double dx = cx - eye.x, dy = ay - eye.y, dz = cz - eye.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));
        return new float[]{yaw, Mth.clamp(pitch, -90f, 90f)};
    }

    private static float speedDeg() {
        double lo = StorageFinderConfig.auraMinRot, hi = StorageFinderConfig.auraMaxRot;
        if (lo > hi) { double t = lo; lo = hi; hi = t; }
        double s = lo + Math.random() * (hi - lo);
        return (float) Math.max(0.5, s) * 18f;           // skala 0-10 -> stopnie na tick (10 = natychmiast)
    }

    private static float approach(float cur, float target, float max) {
        float d = Mth.wrapDegrees(target - cur);
        if (Math.abs(d) <= max) return cur + d;
        return cur + Math.signum(d) * max;
    }

    /** Szum "Polar" — suma kilku losowych harmonicznych (jak w oryginale, ale ograniczona, by nie gubić celu). */
    private static float[] polarNoise(float speed) {
        double amp = Math.max(0.5, Math.min(4.0, speed / 18.0 * 0.5));
        double yo = 0, po = 0;
        for (int i = 0; i < 3; i++) {
            double ph = RNG.nextDouble() * Math.PI * 2.0;
            double a = RNG.nextDouble() * amp / (1 + i);
            yo += a * Math.sin(ph);
            po += a * Math.cos(ph * 0.7);
        }
        return new float[]{(float) yo, (float) (po * 0.5)};
    }

    // ---------------------------------------------------------------- START ticka

    public static void tickStart(Minecraft mc) {
        pending = null;
        rotated = false;
        targeting = false;
        if (!enabled || !ready(mc)) {
            hasAim = false;
            return;
        }

        LocalPlayer p = mc.player;
        LivingEntity best = findTarget(p, mc.level);
        if (best == null) {
            hasAim = false;
            return;
        }
        pending = best;
        targeting = true;
        lastTarget = best;
        lastSeen = System.currentTimeMillis();

        int mode = StorageFinderConfig.auraHitMode;
        boolean critMode = mode == 2 || mode == 3;
        boolean inKill = best.getBoundingBox().distanceToSqr(p.getEyePosition()) <= killRange() * killRange();
        if (critMode && StorageFinderConfig.auraStopSprint && !p.onGround() && inKill) {
            mc.options.keySprint.setDown(false);
            p.setSprinting(false);
        }

        int rm = StorageFinderConfig.auraRotMode;
        if (rm == 3 || Freecam.isActive()) return;

        if (!hasAim) {
            aimYaw = p.getYRot();
            aimPitch = p.getXRot();
            hasAim = true;
        }

        float[] t = aimAt(p, best);
        float speed = speedDeg();
        if (rm == 1) {
            float[] n = polarNoise(speed);
            t[0] += n[0];
            t[1] = Mth.clamp(t[1] + n[1], -90f, 90f);
        }
        if (rm == 2 || speed >= 180f) {
            aimYaw = t[0];
            aimPitch = t[1];
        } else {
            aimYaw = approach(aimYaw, t[0], speed);
            aimPitch = Mth.clamp(approach(aimPitch, t[1], speed), -90f, 90f);
        }

        savedYaw = p.getYRot();
        savedPitch = p.getXRot();
        savedYawO = p.yRotO;
        savedPitchO = p.xRotO;
        rotated = true;

        p.setYRot(aimYaw);
        p.setXRot(aimPitch);
        p.yRotO = aimYaw;
        p.xRotO = aimPitch;
    }

    // ---------------------------------------------------------------- END ticka

    /** Czy promień z aktualnego obrotu trafia w hitbox celu. */
    private static boolean rayHits(LocalPlayer p, LivingEntity t, double range) {
        Vec3 eye = p.getEyePosition();
        double yr = Math.toRadians(p.getYRot()), pr = Math.toRadians(p.getXRot());
        double cp = Math.cos(pr);
        Vec3 dir = new Vec3(-Math.sin(yr) * cp, -Math.sin(pr), Math.cos(yr) * cp);
        AABB bb = t.getBoundingBox();
        if (bb.contains(eye)) return true;
        return bb.clip(eye, eye.add(dir.scale(range + 0.05))).isPresent();
    }

    public static void tickEnd(Minecraft mc) {
        LocalPlayer p = mc.player;
        try {
            if (pending != null && enabled && p != null && ready(mc) && modeOkEnd(p)) {
                Vec3 eye = p.getEyePosition();
                double kill = killRange();
                if (validTarget(p, pending, eye, kill)) {
                    boolean timingOk = p.getAttackStrengthScale(0.5f) >= StorageFinderConfig.auraMinCharge / 100f - 0.02f
                            && pending.hurtTime <= 1;
                    if (timingOk) {
                        if (StorageFinderConfig.auraMissRate > 0
                                && Math.random() * 100.0 < StorageFinderConfig.auraMissRate) {
                            // celowe pudło — pomijamy ten cios
                        } else if (!StorageFinderConfig.auraRaycast || rayHits(p, pending, kill)) {
                            AutoTool.swapToSword(p);
                            boolean spr = p.isSprinting();
                            Vec3 dm = p.getDeltaMovement();
                            mc.gameMode.attack(p, pending);
                            p.swing(InteractionHand.MAIN_HAND);
                            if (StorageFinderConfig.auraKeepSprint && spr) {
                                p.setSprinting(true);
                                p.setDeltaMovement(dm);
                            }
                        }
                    }
                }
            }
        } finally {
            if (rotated && p != null) {
                p.setYRot(savedYaw);
                p.setXRot(savedPitch);
                p.yRotO = savedYawO;
                p.xRotO = savedPitchO;
            }
            pending = null;
            rotated = false;
        }
    }
}
