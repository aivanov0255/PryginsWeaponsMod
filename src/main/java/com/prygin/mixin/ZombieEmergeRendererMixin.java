package com.prygin.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.prygin.common.IEmergingRenderState;
import com.prygin.common.ModAttachments;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class ZombieEmergeRendererMixin {

    @ModifyReturnValue(method = "getRenderOffset", at = @At("RETURN"))
    private Vec3 modifyOffset(Vec3 original, EntityRenderState state) {
        if (state instanceof ZombieRenderState zombieState) {
            IEmergingRenderState emergingState = (IEmergingRenderState) zombieState;

            if (emergingState.isEmerging()) {
                float progress = emergingState.getEmergeProgress();
                return original.add(0, -1.8 * (1.0 - progress), 0);
            }
        }
        return original;
    }
}

