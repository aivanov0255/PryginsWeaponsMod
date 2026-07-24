package com.prygin.trap;

import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public abstract class MobTrap extends AbstractTrap {
    private EntityType<?> mobType;
    private int num;

    private int age = 0;

    private boolean frozen;

    public MobTrap(EntityType<?> type, Level level, EntityType<?> mobType, int num, boolean isFrozen) {
        super(type, level);

        this.mobType = mobType;
        this.num = num;

        this.frozen = isFrozen;
    }

    @Override
    public void tick() {
        super.tick();

        age++;
    }

    public EntityType<?> getMobType() {
        return mobType;
    }

    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public abstract Item getTrapItem();

    @Override
    public RawAnimation getActivateAnimation() {
        return RawAnimation.begin().then("activate", LoopType.HOLD_ON_LAST_FRAME);
    }

    @Override
    public void onActivate(ServerPlayer player, ServerLevel level) {
        for (int i = 0; i < num; i++) {
            Entity entity = mobType.create(level, EntitySpawnReason.TRIGGERED);
            entity.snapTo(getX(), getY(), getZ());

            assert entity != null;
            level.addFreshEntity(entity);
        }
    }
}

