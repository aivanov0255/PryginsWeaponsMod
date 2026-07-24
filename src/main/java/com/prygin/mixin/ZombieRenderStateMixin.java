package com.prygin.mixin;

import com.prygin.common.IEmergingRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZombieRenderState.class)
public class ZombieRenderStateMixin implements IEmergingRenderState {

    @Unique
    private boolean guns$isEmerging;

    @Unique
    private float guns$emergeProgress;

    @Override
    public boolean isEmerging() {
        return this.guns$isEmerging;
    }

    @Override
    public void setEmerging(boolean emerging) {
        this.guns$isEmerging = emerging;
    }

    @Override
    public float getEmergeProgress() {
        return this.guns$emergeProgress;
    }

    @Override
    public void setEmergeProgress(float progress) {
        this.guns$emergeProgress = progress;
    }
}