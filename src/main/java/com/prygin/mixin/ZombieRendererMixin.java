package com.prygin.mixin;

import com.prygin.common.IEmergingRenderState;
import com.prygin.common.ModAttachments;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractZombieRenderer.class)
public class ZombieRendererMixin {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void copyEmergingData(Zombie entity, ZombieRenderState state, float tickDelta, CallbackInfo ci) {
        IEmergingRenderState emergingState = (IEmergingRenderState) state;

        boolean emerging = entity.getAttachedOrElse(ModAttachments.EMERGING, false);
        emergingState.setEmerging(emerging);

        if (emerging) {
            float currentTicks = entity.getAttachedOrElse(ModAttachments.EMERGE_TICKS, 0);
            // Calculate a smooth float progress using tickDelta
            float smoothTicks = currentTicks + tickDelta;
            emergingState.setEmergeProgress(smoothTicks / 40.0f);
        }
    }
}