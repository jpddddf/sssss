package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Auto Tool (port z Simp): najlepsze narzędzie na pasku do bloku, a przy ataku najlepszy miecz. */
public final class AutoTool {
    private static boolean enabled = false;

    private AutoTool() {}

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean v) { enabled = v; }

    private static double swordDamage(ItemStack s) {
        final double[] v = {0.0};
        s.forEachModifier(EquipmentSlot.MAINHAND, (attr, mod) -> {
            if (attr.value() == Attributes.ATTACK_DAMAGE.value()) v[0] += mod.amount();
        });
        return v[0];
    }

    /** Wywoływane też z Aury tuż przed uderzeniem. */
    public static void swapToSword(LocalPlayer p) {
        if (!enabled || !StorageFinderConfig.autoToolSword) return;
        double best = 0.0;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.getInventory().getItem(i);
            if (s.isEmpty() || !s.is(ItemTags.SWORDS)) continue;
            double d = swordDamage(s);
            if (d > best) {
                best = d;
                slot = i;
            }
        }
        if (slot != -1 && p.getInventory().getSelectedSlot() != slot) p.getInventory().setSelectedSlot(slot);
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (!enabled || p == null || mc.level == null || mc.screen != null) return;
        if (StorageFinderConfig.autoToolSneak && !p.isShiftKeyDown()) return;
        if (!mc.options.keyAttack.isDown()) return;

        HitResult hit = mc.hitResult;
        if (hit instanceof EntityHitResult) {
            swapToSword(p);
            return;
        }
        if (!(hit instanceof BlockHitResult bhr) || hit.getType() != HitResult.Type.BLOCK) return;

        var state = mc.level.getBlockState(bhr.getBlockPos());
        float best = 1.0f;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack s = p.getInventory().getItem(i);
            if (s.isEmpty()) continue;
            float sp = s.getDestroySpeed(state);
            if (sp > best) {
                best = sp;
                slot = i;
            }
        }
        if (slot != -1 && p.getInventory().getSelectedSlot() != slot) p.getInventory().setSelectedSlot(slot);
    }
}
