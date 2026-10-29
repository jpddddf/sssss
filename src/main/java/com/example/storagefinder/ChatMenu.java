package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * Prawy przycisk myszy w czacie (gdziekolwiek) otwiera małą listę "Visuals" z przełącznikami modułów wizualnych.
 * Tu też przeciąga się Target ESP (HUD). Wejście jest odpytywane (bez zdarzeń ekranu), więc nie zależy od API zdarzeń.
 */
public final class ChatMenu {
    private static final int MW = 132, ROW = 15, HEAD = 18;

    private static boolean open = false;
    private static int px, py;
    private static boolean prevL = false, prevR = false;
    private static boolean dragging = false;
    private static int offX, offY;

    private ChatMenu() {}

    private static List<Module> rows() {
        List<Module> l = new ArrayList<>();
        for (Module m : Modules.ALL) {
            if (m.category.equals("Render") || m == Modules.ARRAY_LIST || m.name.equals("Info")) l.add(m);
        }
        return l;
    }

    private static int menuH(int n) { return HEAD + n * ROW + 4; }

    static void update(Minecraft mc) {
        if (!(mc.screen instanceof ChatScreen)) {
            if (dragging) StorageFinderConfig.save();
            open = false;
            dragging = false;
            prevL = false;
            prevR = false;
            return;
        }
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();
        int mx = (int) (mc.mouseHandler.xpos() * sw / Math.max(1, mc.getWindow().getScreenWidth()));
        int my = (int) (mc.mouseHandler.ypos() * sh / Math.max(1, mc.getWindow().getScreenHeight()));
        boolean l = mc.mouseHandler.isLeftPressed(), r = mc.mouseHandler.isRightPressed();
        boolean lEdge = l && !prevL, rEdge = r && !prevR;
        prevL = l;
        prevR = r;

        List<Module> rows = rows();
        if (rEdge) {
            open = true;
            px = Mth.clamp(mx, 0, sw - MW);
            py = Mth.clamp(my, 0, Math.max(0, sh - menuH(rows.size())));
            dragging = false;
            return;
        }

        if (lEdge) {
            boolean inMenu = open && Ui.hit(mx, my, px, py, MW, menuH(rows.size()));
            if (inMenu) {
                int idx = (my - py - HEAD) / ROW;
                if (my >= py + HEAD && idx >= 0 && idx < rows.size()) {
                    Module m = rows.get(idx);
                    m.setEnabled(!m.isEnabled());
                    StorageFinderConfig.save();
                }
            } else {
                open = false;
                if (StorageFinderConfig.targetHud && Ui.hit(mx, my, TargetHud.x(mc), TargetHud.y(mc), TargetHud.W, TargetHud.H)) {
                    dragging = true;
                    offX = mx - TargetHud.x(mc);
                    offY = my - TargetHud.y(mc);
                }
            }
        }

        if (dragging) {
            if (l) {
                StorageFinderConfig.targetHudX = Mth.clamp(mx - offX, 0, Math.max(0, sw - TargetHud.W));
                StorageFinderConfig.targetHudY = Mth.clamp(my - offY, 0, Math.max(0, sh - TargetHud.H));
            } else {
                dragging = false;
                StorageFinderConfig.save();
            }
        }
    }

    static void draw(GuiGraphics g, Minecraft mc) {
        if (!open || !(mc.screen instanceof ChatScreen)) return;
        List<Module> rows = rows();
        int sw = mc.getWindow().getGuiScaledWidth(), sh = mc.getWindow().getGuiScaledHeight();
        int mx = (int) (mc.mouseHandler.xpos() * sw / Math.max(1, mc.getWindow().getScreenWidth()));
        int my = (int) (mc.mouseHandler.ypos() * sh / Math.max(1, mc.getWindow().getScreenHeight()));
        int h = menuH(rows.size());
        int accent = 0xFF000000 | StorageFinderConfig.alAccent;

        Ui.rrect(g, px - 1, py - 1, MW + 2, h + 2, 7, 0xFF2A313B);
        Ui.rrect(g, px, py, MW, h, 6, 0xF20E1116);
        KruxFont.draw(g, mc.font, "Visuals", px + 8, py + 5, 0xFFA3ABB3);
        g.fill(px + 6, py + HEAD - 2, px + MW - 6, py + HEAD - 1, 0xFF2A313B);

        for (int i = 0; i < rows.size(); i++) {
            Module m = rows.get(i);
            int ry = py + HEAD + i * ROW;
            if (Ui.hit(mx, my, px + 3, ry, MW - 6, ROW)) g.fill(px + 3, ry, px + MW - 3, ry + ROW, 0xFF1A2027);
            boolean on = m.isEnabled();
            KruxFont.draw(g, mc.font, m.name, px + 8, ry + 3, on ? 0xFFF4F7FA : 0xFF8A939D);
            int tx = px + MW - 26, ty = ry + 3;
            Ui.rrect(g, tx, ty, 18, 9, 4, on ? accent : 0xFF2F3640);
            Ui.rrect(g, on ? tx + 10 : tx + 1, ty + 1, 7, 7, 3, 0xFFFFFFFF);
        }
    }
}
