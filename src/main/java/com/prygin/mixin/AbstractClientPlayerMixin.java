package com.prygin.mixin;

import com.prygin.playeranimations.WaveAnimationAccess;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements WaveAnimationAccess {
    @Unique
    private final AnimationState waveAnimationState = new AnimationState();

    @Override
    public AnimationState waveAnimationState() {
        return this.waveAnimationState;
    }
}
