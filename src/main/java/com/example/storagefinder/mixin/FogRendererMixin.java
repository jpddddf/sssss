package com.example.storagefinder.mixin;

import com.example.storagefinder.Fog;
import com.example.storagefinder.StorageFinderConfig;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Podmienia parametry mgły 1.21.11 tuż przed zapisem do bufora (FogRenderer#updateBuffer):
 * kolor oraz początek/koniec mgły środowiskowej. Jeśli sygnatura się różni, wstrzyknięcie jest po cichu pomijane.
 */
@Mixin(net.minecraft.client.renderer.fog.FogRenderer.class)
public abstract class FogRendererMixin {
    @ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true)
    private static Vector4f krux$color(Vector4f c) {
        if (!Fog.active()) return c;
        int rgb = StorageFinderConfig.fogColor;
        c.set(((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f, 1f);
        return c;
    }

    @ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static float krux$envStart(float v) {
        return Fog.active() ? Fog.start() : v;
    }

    @ModifyVariable(method = "updateBuffer", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static float krux$envEnd(float v) {
        return Fog.active() ? Fog.end() : v;
    }
}
