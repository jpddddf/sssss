package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

/**
 * Auto Armor (port z Simp). Tryb Open: działa przy otwartym ekwipunku. Tryb Spoof: działa bez otwierania
 * (wysyła kliknięcia ekwipunku), ale nie gdy Aura ma cel. Jedna akcja na interwał "Speed".
 */
public final class AutoArmor {
    private static boolean enabled = false;
    private static long openedAt = 0L;
    private static long lastAction = 0L;
    private static boolean wasOpen = false;

    private static final EquipmentSlot[] TYPES = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private AutoArmor() {}

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean v) { enabled = v; }

    /** Typ pancerza przedmiotu albo null. */
    private static EquipmentSlot armorSlot(ItemStack s) {
        if (s.isEmpty()) return null;
        Equippable eq = s.get(DataComponents.EQUIPPABLE);
        if (eq == null) return null;
        EquipmentSlot sl = eq.slot();
        return (sl == EquipmentSlot.HEAD || sl == EquipmentSlot.CHEST
                || sl == EquipmentSlot.LEGS || sl == EquipmentSlot.FEET) ? sl : null;
    }

    private static double score(ItemStack s, EquipmentSlot slot) {
        if (s.isEmpty()) return -1.0;
        final double[] v = {0.0};
        s.forEachModifier(slot, (attr, mod) -> {
            if (attr.value() == Attributes.ARMOR.value()) v[0] += mod.amount() * 10.0;
            else if (attr.value() == Attributes.ARMOR_TOUGHNESS.value()) v[0] += mod.amount() * 2.0;
        });
        if (s.isDamageableItem()) v[0] -= (double) s.getDamageValue() / Math.max(1, s.getMaxDamage());
        return v[0];
    }

    private static int menuSlot(EquipmentSlot sl) {
        return switch (sl) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            default -> 8;
        };
    }

    private static void click(Minecraft mc, LocalPlayer p, int slot) {
        mc.gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, slot, 0, ClickType.QUICK_MOVE, p);
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        boolean open = mc.screen instanceof InventoryScreen;
        long now = System.currentTimeMillis();
        if (open && !wasOpen) openedAt = now;
        wasOpen = open;

        if (!enabled || p == null || mc.gameMode == null || mc.level == null) return;

        boolean spoof = StorageFinderConfig.armorMode == 1;
        if (spoof) {
            if (mc.screen != null || Aura.hasTarget()) return;
        } else {
            if (!open || now - openedAt < StorageFinderConfig.armorStartDelay) return;
        }
        if (now - lastAction < StorageFinderConfig.armorSpeed) return;

        for (EquipmentSlot type : TYPES) {
            int worn = menuSlot(type);
            ItemStack wornStack = p.inventoryMenu.getSlot(worn).getItem();
            double wornScore = score(wornStack, type);

            int bestSlot = -1;
            double best = wornScore;
            for (int i = 9; i < 45; i++) {
                ItemStack st = p.inventoryMenu.getSlot(i).getItem();
                if (armorSlot(st) != type) continue;
                double sc = score(st, type);
                if (sc > best) {
                    best = sc;
                    bestSlot = i;
                }
            }
            if (bestSlot == -1) continue;

            if (!wornStack.isEmpty()) {
                click(mc, p, worn);          // zdejmij gorszy (shift-klik do ekwipunku)
            } else {
                click(mc, p, bestSlot);      // załóż lepszy (shift-klik)
            }
            lastAction = now;
            return;
        }
    }
}
