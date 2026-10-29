package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/** Powiadomienia przy włączaniu/wyłączaniu modułów (prawy dolny róg, wsuwają się i znikają). */
public final class Notifications {
    private record N(String title, String desc, boolean on, long t0) {}

    private static final List<N> list = new ArrayList<>();
    private static final long LIFE = 2200L, ANIM = 250L;

    private Notifications() {}

    static void onToggle(Module m) {
        if (!StorageFinderConfig.xb("m.notifications", true) || m.name.equals("Notifications")) return;
        if (Minecraft.getInstance().level == null) return;
        list.add(new N(m.name, m.isEnabled() ? "Enabled" : "Disabled", m.isEnabled(), System.currentTimeMillis()));
        while (list.size() > 6) list.remove(0);
    }

    static void render(GuiGraphics g, Minecraft mc) {
        long now = System.currentTimeMillis();
        list.removeIf(n -> now - n.t0() > LIFE);
        if (list.isEmpty()) return;
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();
        int accent = 0xFF000000 | StorageFinderConfig.alAccent;
        int idx = 0;
        for (int i = list.size() - 1; i >= 0; i--, idx++) {
            N n = list.get(i);
            long age = now - n.t0();
            float in = Mth.clamp(age / (float) ANIM, 0f, 1f), out = Mth.clamp((LIFE - age) / (float) ANIM, 0f, 1f);
            float k = Math.min(in, out);
            k = 1f - (1f - k) * (1f - k) * (1f - k);
            int w = Math.max(KruxFont.width(mc.font, n.title()), KruxFont.width(mc.font, n.desc())) + 26;
            int h = 28;
            int x = Math.round(sw - w - 6 + (1f - k) * (w + 10));
            int y = sh - 40 - idx * (h + 5);
            Ui.rrect(g, x, y, w, h, 5, 0xEB0E1116);
            g.fill(x + 4, y + 6, x + 6, y + h - 6, n.on() ? accent : 0xFF6B7480);
            KruxFont.draw(g, mc.font, n.title(), x + 12, y + 6, 0xFFF4F7FA);
            KruxFont.draw(g, mc.font, n.desc(), x + 12, y + 16, n.on() ? accent : 0xFFA3ABB3);
        }
    }
}
