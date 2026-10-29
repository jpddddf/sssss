package com.example.storagefinder.mixin;

import com.example.storagefinder.Blink;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Blink: przechwytuje wysyłane pakiety ruchu. */
@Mixin(Connection.class)
public abstract class ConnectionSendMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void krux$send(Packet<?> packet, CallbackInfo ci) {
        if (Blink.intercept(packet)) ci.cancel();
    }
}
