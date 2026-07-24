package com.prygin.item;

import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;

public class EffectTrapItem extends TrapItem {
    MobEffect effect;

    public EffectTrapItem(Properties properties, EntityType<? extends AbstractTrap> entityType, Holder<MobEffect> effect) {
        super(properties, entityType);

        this.effect = effect.value();
    }

    public MobEffect getEffect() {
        return effect;
    }
}
