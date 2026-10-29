package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * "Target ESP" (HUD): prostokąt z nazwą, HP i pancerzem celu Aury. Pojawia się, gdy bijesz kogoś Aurą;
 * w czacie widać go zawsze (przykładowy) i można go przeciągnąć w dowolne miejsce.
 */
public final class TargetHud {
    static final int W = 150, H = 50;

    private static LivingEntity last = null;
    private static float hpShown = 1f;
    private static float armorShown = 0f;

    private TargetHud() {}

    static int x(Minecraft mc) {
        int sw = mc.getWindow().getGuiScaledWidth();
        int v = StorageFinderConfig.targetHudX < 0 ? sw / 2 + 30 : StorageFinderConfig.targetHudX;
        return Mth.clamp(v, 0, Math.max(0, sw - W));
    }

    static int y(Minecraft mc) {
        int sh = mc.getWindow().getGuiScaledHeight();
        int v = StorageFinderConfig.targetHudY < 0 ? sh / 2 + 30 : StorageFinderConfig.targetHudY;
        return Mth.clamp(v, 0, Math.max(0, sh - H));
    }

    private static int argb(int rgb, float a) {
        return (Mth.clamp(Math.round(a * 255f), 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    static void render(GuiGraphics g, Minecraft mc) {
        if (!StorageFinderConfig.targetHud) return;
        boolean chat = mc.screen instanceof ChatScreen;
        LivingEntity t = Aura.targetEspEntity();
        float fade = t != null ? Aura.targetEspFade() : 0f;
        if (t == null && !chat) {
            last = null;
            return;
        }
        if (t == null) fade = 1f;                       // przykład podczas przesuwania w czacie

        String name = t != null ? t.getName().getString() : "Player";
        float hp = t != null ? t.getHealth() : 20f;
        float max = t != null ? Math.max(1f, t.getMaxHealth()) : 20f;
        int armor = t != null ? t.getArmorValue() : 12;

        if (t != last) {
            hpShown = Mth.clamp(hp / max, 0f, 1f);
            armorShown = Mth.clamp(armor / 20f, 0f, 1f);
            last = t;
        }
        hpShown += (Mth.clamp(hp / max, 0f, 1f) - hpShown) * 0.2f;
        armorShown += (Mth.clamp(armor / 20f, 0f, 1f) - armorShown) * 0.2f;

        int x = x(mc), y = y(mc);
        int accent = StorageFinderConfig.alAccent;
        Ui.rrect(g, x - 1, y - 1, W + 2, H + 2, 8, argb(accent, 0.55f * fade));
        Ui.rrect(g, x, y, W, H, 7, argb(0x0E1116, 0.86f * fade));

        String shown = KruxFont.trim(mc.font, name, W - 16);
        KruxFont.draw(g, mc.font, shown, x + 8, y + 6, argb(0xF4F7FA, fade));

        int bx = x + 8, bw = W - 16;
        g.fill(bx, y + 19, bx + bw, y + 25, argb(0x2A313B, fade));
        int hpCol = ((int) (255 * (1f - hpShown)) << 16) | ((int) (200 * hpShown + 40) << 8) | 60;
        g.fill(bx, y + 19, bx + Math.round(bw * hpShown), y + 25, argb(hpCol, fade));

        String hpText = "HP " + Math.round(hp * 10f) / 10f + " / " + Math.round(max);
        KruxFont.draw(g, mc.font, hpText, x + 8, y + 28, argb(0xC8CED6, fade));
        String arText = "Armor " + armor;
        KruxFont.draw(g, mc.font, arText, x + W - 8 - KruxFont.width(mc.font, arText), y + 28, argb(0x9FC4E8, fade));

        g.fill(bx, y + 40, bx + bw, y + 43, argb(0x2A313B, fade));
        g.fill(bx, y + 40, bx + Math.round(bw * armorShown), y + 43, argb(0x7FB8E8, fade));
    }
}
