package com.prygin.item;

import com.geckolib.animatable.GeoItem;
import com.prygin.item.components.ModComponents;
import com.prygin.item.shulker_blaster.ShulkerBlaster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AmmoItem extends Item {
    GunItem gun;
    SoundEvent reloadSound;

    public AmmoItem(Properties properties, GunItem gun, SoundEvent reloadSound) {
        this.gun = gun;
        this.reloadSound = reloadSound;
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack.getItem() instanceof GunItem gunItem) {
                if (gunItem.equals(gun)) {
                    ItemStack item = player.getItemInHand(hand);

                    ItemStack gunItemStack = itemStack;

                    if (gunItemStack.get(ModComponents.AMMO) >= gunItem.gunProperties.maxAmmo()) continue;

                    int amount = gunItem.gunProperties.maxAmmo() - gunItemStack.get(ModComponents.AMMO);

                    if (reloadSound != null) level.playSound(null, player, reloadSound, SoundSource.PLAYERS, 1.0f, 1.0f);

                    if (item.count() <= amount) {
                        gunItemStack.set(ModComponents.AMMO, gunItemStack.get(ModComponents.AMMO) + item.count());
                        item.setCount(0);
                    } else {
                        gunItemStack.set(ModComponents.AMMO, gunItem.gunProperties.maxAmmo());
                        item.setCount(item.count() - amount);
                    }

                    if (gunItemStack.getItem() instanceof ShulkerBlaster shulkerBlaster && !level.isClientSide()) {
                        long instanceId = GeoItem.getOrAssignId(gunItemStack, (ServerLevel) level);
                        shulkerBlaster.triggerAnim(player, instanceId, ShulkerBlaster.SHOOT_CONTROLLER_NAME, "reappear");
                    }

                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
