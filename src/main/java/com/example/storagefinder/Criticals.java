package com.example.storagefinder;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

/** Criticals (port z Simp): Visual (cząsteczki), Legit (skok po ciosie), NCP (pakiety pozycji). */
public final class Criticals {
    private static boolean jumpFlag = false, holding = false;

    private Criticals() {}

    static boolean on() { return StorageFinderConfig.xb("m.criticals", false) && Krux.licensed(); }

    public static void init() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (level.isClientSide()) onAttack(entity);
            return InteractionResult.PASS;
        });
    }

    private static void onAttack(Entity target) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (!on() || p == null) return;
        switch (StorageFinderConfig.xi("cr.mode", 2)) {
            case 0 -> p.crit(target);
            case 1 -> {
                if (p.onGround() && !p.onClimbable() && !p.isInWater() && !p.isPassenger() && !mc.options.keyJump.isDown())
                    jumpFlag = true;
            }
            default -> {
                boolean natural = p.fallDistance > 0.0 && !p.onGround() && !p.onClimbable() && !p.isInWater()
                        && !p.hasEffect(MobEffects.BLINDNESS) && !p.isPassenger();
                if (natural || p.connection == null) return;
                p.connection.send(new ServerboundMovePlayerPacket.Pos(p.getX(), p.getY() + 0.000000271875, p.getZ(), false, false));
                p.connection.send(new ServerboundMovePlayerPacket.Pos(p.getX(), p.getY(), p.getZ(), false, false));
            }
        }
    }

    public static void tickStart(Minecraft mc) {
        if (!on() || mc.player == null) {
            jumpFlag = false;
            return;
        }
        if (jumpFlag) {
            jumpFlag = false;
            if (mc.player.onGround()) {
                mc.options.keyJump.setDown(true);
                holding = true;
            }
        }
    }

    public static void tickEnd(Minecraft mc) {
        if (holding) {
            mc.options.keyJump.setDown(false);
            holding = false;
        }
    }
}
