package com.prygin.mixin;

import com.prygin.access.HumanoidRenderStateExtension;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void guns$captureHeldItems(T entity, S state, float partialTick, CallbackInfo ci) {
        if (state instanceof HumanoidRenderState && state instanceof HumanoidRenderStateExtension extension) {
            extension.guns$setMainHandStack(entity.getMainHandItem());
            extension.guns$setOffHandStack(entity.getOffhandItem());
        }
    }
}