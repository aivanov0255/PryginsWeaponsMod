package com.prygin.item;

import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;

public class MobTrapItem extends TrapItem {
    EntityType mobType;

    public MobTrapItem(Properties properties, EntityType<? extends AbstractTrap> entityType, EntityType mobType) {
        super(properties, entityType);

        this.mobType = mobType;
    }

    public EntityType getMobType() {
        return mobType;
    }
}
