package com.prygin.common;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.monster.zombie.Zombie;

public final class ZombieEmergeData {
    public static final EntityDataAccessor<Boolean> EMERGING =
            SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
}
