package com.example.storagefinder;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

/** Czcionka interfejsu: w wersji dla klientów normalna (Inter), w wersji developerskiej zostaje Minecraftowa. */
final class KruxFont {
    static final boolean CUSTOM = !Krux.DEV_BUILD;
    private static final Style STYLE = Style.EMPTY.withFont(
            new FontDescription.Resource(Identifier.fromNamespaceAndPath("storagefinder", "krux")));

    private KruxFont() {}

    static MutableComponent c(String s) {
        return CUSTOM ? Component.literal(s).withStyle(STYLE) : Component.literal(s);
    }

    static MutableComponent bold(String s) {
        return c(s).withStyle(ChatFormatting.BOLD);
    }

    static int width(Font f, Component s) {
        return f.width(s);
    }

    static void draw(GuiGraphics g, Font f, Component s, int x, int y, int color) {
        g.drawString(f, s, x, y, color);
    }

    static int width(Font f, String s) {
        return CUSTOM ? f.width(c(s)) : f.width(s);
    }

    static void draw(GuiGraphics g, Font f, String s, int x, int y, int color) {
        if (CUSTOM) g.drawString(f, c(s), x, y, color);
        else g.drawString(f, s, x, y, color);
    }

    static String trim(Font f, String s, int w) {
        if (!CUSTOM) return f.plainSubstrByWidth(s, w);
        while (s.length() > 1 && width(f, s) > w) s = s.substring(0, s.length() - 1);
        return s;
    }
}
