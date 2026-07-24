package com.prygin.item;

import com.prygin.entity.ModEntityTypes;
import com.prygin.entity.nijastar.NinjaStarEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NinjaStarItem extends Item {
    public NinjaStarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!player.isCreative()) itemStack.setCount(itemStack.getCount() - 1);

            NinjaStarEntity star = new NinjaStarEntity(ModEntityTypes.NINJA_STAR, level);

            star.snapTo(player.getX(), player.getY() + player.getEyeHeight() - 0.1, player.getZ(), player.getYRot(), player.getXRot());

            star.setOwner(player);

            level.addFreshEntity(star);

            star.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 0f);
        }

        return InteractionResult.SUCCESS;
    }
}
