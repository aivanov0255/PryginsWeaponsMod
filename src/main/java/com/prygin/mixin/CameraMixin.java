package com.prygin.mixin;

import com.prygin.zoom.ZoomManager;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(
            method = "modifyFovBasedOnDeathOrFluid",
            at = @At("TAIL"),
            cancellable = true
    )
    private void onModifyFov(float partialTicks, float originalFov, CallbackInfoReturnable<Float> cir) {
        if (!ZoomManager.isZooming()) return;

        float fov = cir.getReturnValue();
        cir.setReturnValue(fov * ZoomManager.getSmoothedZoomScale());
    }
}