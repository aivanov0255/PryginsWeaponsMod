package com.prygin.item;

import com.prygin.entity.ModEntityTypes;
import com.prygin.entity.boomerang.BoomerangEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BoomerangItem extends Item {
    public BoomerangItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            player.getCooldowns().addCooldown(stack, 20);
            stack.setCount(stack.getCount() - 1);

            BoomerangEntity entity = new BoomerangEntity(ModEntityTypes.BOOMERANG, level);
            entity.setOwnerId(player.getUUID());
            entity.setStartingRot((float) Math.toRadians(player.getXRot()));
            entity.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            level.addFreshEntity(entity);
        }

        return InteractionResult.SUCCESS;
    }
}
