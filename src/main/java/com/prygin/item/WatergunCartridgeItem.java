package com.prygin.item;

import com.prygin.item.components.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class WatergunCartridgeItem extends AmmoItem {
    public WatergunCartridgeItem(Properties properties, GunItem gun, SoundEvent reloadSound) {
        super(properties, gun, reloadSound);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);

        if (level.getBlockState(player.blockPosition()).getBlock().equals(Blocks.WATER)) {
            if (item.get(ModComponents.AMMO) != 100) level.playSound(null, player, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, (float)Math.random()/2 + 0.75f);

            item.set(ModComponents.AMMO, 100);
        } else {
            for (ItemStack itemStack : player.getInventory()) {
                if (itemStack.getItem() instanceof GunItem gunItem) {
                    if (gunItem.equals(gun)) {
                        Integer tempHolder = itemStack.get(ModComponents.AMMO);
                        itemStack.set(ModComponents.AMMO, item.get(ModComponents.AMMO));
                        item.set(ModComponents.AMMO, tempHolder);

                        if (reloadSound != null)
                            level.playSound(null, player, reloadSound, SoundSource.PLAYERS, 1.0f, 1.0f);

                        return InteractionResult.CONSUME;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        Integer ammo = stack.get(ModComponents.AMMO);
        if (ammo != null) {
            int num = ammo / 10;
            textConsumer.accept(Component.literal(ammo + "% ")
                    .append(
                            Component.literal("█".repeat(num))
                                    .withColor(Integer.parseInt("0084FF", 16))
                    )
                    .append(
                            Component.literal("█".repeat(10-num))
                                    .withColor(Integer.parseInt("001E3B", 16))
                    )
            );
        }
    }
}
