package com.example.storagefinder.mixin;

import com.example.storagefinder.Hitboxes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hitboxes: dodaje margines do hitboxa przy celowaniu (getPickRadius). Pomijane, jeśli metoda ma inną nazwę. */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getPickRadius", at = @At("RETURN"), cancellable = true)
    private void krux$pick(CallbackInfoReturnable<Float> cir) {
        float e = Hitboxes.expand();
        if (e <= 0f || (Object) this == Minecraft.getInstance().player) return;
        cir.setReturnValue(cir.getReturnValue() + e);
    }
}
