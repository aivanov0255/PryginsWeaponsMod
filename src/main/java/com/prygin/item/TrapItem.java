package com.prygin.item;

import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TrapItem extends Item {
    private final EntityType<? extends AbstractTrap> entityType;

    public TrapItem(Properties properties, EntityType<? extends AbstractTrap> entityType) {
        super(properties);

        this.entityType = entityType;
    }

    public EntityType<? extends AbstractTrap> getEntityType() {
        return entityType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockPos spawnPos = level.getBlockState(clickedPos).getCollisionShape(level, clickedPos).isEmpty()
                ? clickedPos
                : clickedPos.relative(context.getClickedFace());

        AbstractTrap trap = entityType.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        trap.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0F, 0F);

        Player player = context.getPlayer();
        if (player != null) {
            trap.setOwnerId(player.getUUID());
        }

        serverLevel.addFreshEntity(trap);
        context.getItemInHand().shrink(1);

        return InteractionResult.SUCCESS;
    }
}
