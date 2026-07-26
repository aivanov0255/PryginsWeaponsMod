package com.prygin.mixin;

import com.prygin.common.ModAttachments;
import com.prygin.entity.scythe_zombie.ScytheZombie;
import com.prygin.item.ZombieScythe;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieEmergeMixin extends Monster {

    protected ZombieEmergeMixin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                 EntitySpawnReason spawnReason, SpawnGroupData spawnGroupData,
                                 CallbackInfoReturnable<SpawnGroupData> cir) {
        if (this.asLivingEntity() instanceof ScytheZombie) {
            this.setAttached(ModAttachments.EMERGING, true);
            this.setAttached(ModAttachments.EMERGE_TICKS, 0);
            this.setInvulnerable(true);
            this.setNoAi(true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.getAttachedOrElse(ModAttachments.EMERGING, false)) {
            int ticks = this.getAttachedOrElse(ModAttachments.EMERGE_TICKS, 0);

            if (ticks < 40) {
                this.setAttached(ModAttachments.EMERGE_TICKS, ticks + 1);
            } else {
                if (!this.level().isClientSide()) {
                    this.setAttached(ModAttachments.EMERGING, false);
                    this.setNoAi(false);
                    this.setInvulnerable(false);
                }
            }
        }
    }
}