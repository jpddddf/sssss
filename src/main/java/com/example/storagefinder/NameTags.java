package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Name Tags (HUD): nazwa, HP i pancerz nad graczami, rzutowane na ekran (widoczne przez ściany). */
public final class NameTags {
    private NameTags() {}

    static boolean on() { return StorageFinderConfig.xb("m.nametags", false) && Krux.licensed(); }

    static void render(GuiGraphics g, Minecraft mc) {
        if (!on() || mc.level == null || mc.player == null) return;
        Entity cam = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
        float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 eye = cam.getEyePosition(pt);
        double yaw = Math.toRadians(cam.getViewYRot(pt)), pitch = Math.toRadians(cam.getViewXRot(pt));
        Vec3 f = new Vec3(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(pitch) * Math.cos(yaw));
        Vec3 r = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw));
        Vec3 u = r.cross(f);

        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();
        double tan = Math.tan(Math.toRadians(mc.options.fov().get()) / 2.0);
        double aspect = sw / (double) sh;
        float scale = (float) StorageFinderConfig.xd("nt.scale", 1.0);

        for (Player pl : mc.level.players()) {
            if (!pl.isAlive() || pl.isInvisible() || pl == cam || (pl == mc.player && cam == mc.player)) continue;
            Vec3 pos = new Vec3(Mth.lerp(pt, pl.xo, pl.getX()), Mth.lerp(pt, pl.yo, pl.getY()) + pl.getBbHeight() + 0.45,
                    Mth.lerp(pt, pl.zo, pl.getZ()));
            Vec3 d = pos.subtract(eye);
            double cz = d.dot(f);
            if (cz < 0.2) continue;
            double cx = d.dot(r), cy = d.dot(u);
            float sx = (float) (sw / 2.0 + (cx / cz) / (tan * aspect) * (sw / 2.0));
            float sy = (float) (sh / 2.0 - (cy / cz) / tan * (sh / 2.0));
            if (sx < -100 || sx > sw + 100 || sy < -50 || sy > sh + 50) continue;

            String name = pl.getName().getString();
            String dist = StorageFinderConfig.xb("nt.dist", false) ? " " + Math.round(mc.player.distanceTo(pl)) + "m" : "";
            String hp = StorageFinderConfig.xb("nt.hp", true) ? Math.round(pl.getHealth() * 10f) / 10f + "" : "";
            String ar = StorageFinderConfig.xb("nt.armor", true) && pl.getArmorValue() > 0 ? "Armor " + pl.getArmorValue() : "";

            Minecraft m = mc;
            int w1 = KruxFont.width(m.font, name + dist);
            int w2 = hp.isEmpty() ? 0 : KruxFont.width(m.font, hp) + 5;
            int w3 = ar.isEmpty() ? 0 : KruxFont.width(m.font, ar) + 5;
            int total = w1 + w2 + w3;
            float ratio = Mth.clamp(pl.getHealth() / Math.max(1f, pl.getMaxHealth()), 0f, 1f);
            int hpCol = 0xFF000000 | ((int) (255 * (1f - ratio)) << 16) | ((int) (200 * ratio + 40) << 8) | 60;

            g.pose().pushMatrix();
            g.pose().translate(sx, sy);
            g.pose().scale(scale, scale);
            if (StorageFinderConfig.xb("nt.bg", true)) Ui.rrect(g, -total / 2 - 4, -14, total + 8, 13, 3, 0x96000000);
            int x = -total / 2;
            KruxFont.draw(g, m.font, name + dist, x, -12, 0xFFE6EAF0);
            x += w1 + 5;
            if (!hp.isEmpty()) {
                KruxFont.draw(g, m.font, hp, x, -12, hpCol);
                x += w2;
            }
            if (!ar.isEmpty()) KruxFont.draw(g, m.font, ar, x, -12, 0xFF7FB8E8);
            g.pose().popMatrix();
        }
    }
}
