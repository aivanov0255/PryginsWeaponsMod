package com.prygin.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

public class CopyOwnerTargetGoal extends Goal {
    private final Mob mob;
    private final UUID ownerUUID;

    public CopyOwnerTargetGoal(Mob mob, UUID ownerUUID) {
        this.mob = mob;
        this.ownerUUID = ownerUUID;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Player owner = mob.level().getPlayerByUUID(ownerUUID);
        if (owner == null) return false;

        LivingEntity ownerTarget = owner.getLastHurtMob();
        if (ownerTarget == null || !ownerTarget.isAlive()) return false;

        return mob.getTarget() != ownerTarget;
    }

    @Override
    public void start() {
        Player owner = mob.level().getPlayerByUUID(ownerUUID);
        if (owner != null) {
            LivingEntity ownerTarget = owner.getLastHurtMob();
            mob.setTarget(ownerTarget);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }
}