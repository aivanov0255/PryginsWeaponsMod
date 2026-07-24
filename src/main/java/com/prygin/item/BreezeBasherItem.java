package com.prygin.item;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BreezeBasherItem extends Item {
    public BreezeBasherItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(itemStack, 10);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WIND_CHARGE_BURST, player.getSoundSource(), 5, 1);

        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            itemStack.hurtAndBreak(300, player, player.getEquipmentSlotForItem(itemStack));

            double radius = 5.0D;
            AABB box = player.getBoundingBox().inflate(radius);
            List<Entity> entities = level.getEntities(player, box);

            for (Entity entity : entities) {
                Vec3 diff = entity.getEyePosition(1.0F).subtract(player.getEyePosition(1.0F));
                Vec3 normalizedDir = diff.normalize();
                Vec3 pushVelocity = normalizedDir.scale(1.5).add(0.0D, 1.0D, 0.2D);

                entity.setDeltaMovement(pushVelocity);
                entity.hurtMarked = true;

                serverLevel.sendParticles(
                        ParticleTypes.GUST,
                        entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(),
                        3,
                        0.2, 0.2, 0.2,
                        0.0
                );
            }
        }

        return InteractionResult.SUCCESS;
    }
}
