package com.prygin.entity.scythe_zombie;

import com.prygin.entity.goal.CopyOwnerTargetGoal;
import com.prygin.item.ZombieScythe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class ScytheZombie extends Zombie {
    private UUID ownerUUID;

    int time = ZombieScythe.COOLDOWN_TICKS;

    public ScytheZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        time--;
        if (time <= 0) {
            if (!level().isClientSide()) kill((ServerLevel) level());
        }
    }

    public float getScytheCooldown() {
        return time;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        this.targetSelector.getAvailableGoals().forEach(wrapped -> this.targetSelector.removeGoal(wrapped.getGoal()));
        this.targetSelector.addGoal(1, new CopyOwnerTargetGoal(this, ownerUUID));
    }
}