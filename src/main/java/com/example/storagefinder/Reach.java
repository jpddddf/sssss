package com.example.storagefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Reach: zwiększa zasięg interakcji z jednostkami (losowo między Min a Max). */
public final class Reach {
    private static final Identifier ID = Identifier.fromNamespaceAndPath("storagefinder", "reach");

    private Reach() {}

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        AttributeInstance a = p.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (a == null) return;
        boolean on = StorageFinderConfig.xb("m.reach", false) && Krux.licensed();
        if (!on) {
            if (a.hasModifier(ID)) a.removeModifier(ID);
            return;
        }
        double lo = StorageFinderConfig.xd("re.min", 3.0), hi = StorageFinderConfig.xd("re.max", 3.0);
        if (lo > hi) { double t = lo; lo = hi; hi = t; }
        double r = lo + Math.random() * (hi - lo);
        a.addOrReplacePermanentModifier(new AttributeModifier(ID, Math.max(0.0, r - 3.0), AttributeModifier.Operation.ADD_VALUE));
    }
}
