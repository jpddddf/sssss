package com.example.storagefinder.mixin;

import com.example.storagefinder.BackTrack;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Back Track: przechwytuje odbierane pakiety ruchu celu. */
@Mixin(Connection.class)
public abstract class ConnectionReadMixin {
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void krux$read(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        if (BackTrack.intercept(packet)) ci.cancel();
    }
}
