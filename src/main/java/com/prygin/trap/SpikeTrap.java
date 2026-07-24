package com.prygin.trap;

import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.prygin.entity.trap.AbstractTrap;
import com.prygin.item.ModItems;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

public class SpikeTrap extends AbstractTrap {
    public SpikeTrap(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Item getTrapItem() {
        return ModItems.SPIKE_TRAP;
    }

    @Override
    public RawAnimation getActivateAnimation() {
        return RawAnimation.begin().then("activate", LoopType.HOLD_ON_LAST_FRAME);
    }

    @Override
    public void onActivate(ServerPlayer player, ServerLevel level) {
        player.hurtServer(level, player.createDamageSource(), 6);
    }
}
