package com.prygin.mixin;

import com.prygin.rendering.HitDecalRenderer;
import com.prygin.screenshake.ScreenShakeManager;
import com.prygin.zoom.ZoomManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V",
            at = @At("HEAD")
    )
    private void onRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        client.player.setYRot(client.player.getYRot() + ScreenShakeManager.getYawOffset());
        client.player.setXRot(client.player.getXRot() + ScreenShakeManager.getPitchOffset());
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void onGameRendererClose(CallbackInfo ci) {
        HitDecalRenderer.getInstance().close();
    }
}