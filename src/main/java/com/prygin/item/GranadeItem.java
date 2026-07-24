package com.prygin.item;

import com.prygin.entity.Granade;
import com.prygin.item.components.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Consumer;

public class GranadeItem extends Item {
    public GranadeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel) {
            Granade granade = Projectile.spawnProjectileFromRotation(
                    Granade::new,
                    serverLevel,
                    itemStack,
                    player,
                    0.0F,
                    1.5F,
                    1.0F
            );

            granade.setFuse(player.getItemInHand(hand).get(ModComponents.FUSE));
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        DecimalFormat df = new DecimalFormat("#,###.####");

        String fuse = df.format(itemStack.get(ModComponents.FUSE)/context.tickRate());

        builder.accept(Component.translatable("item.guns.fuse.info", fuse).withColor(TextColor.YELLOW));
    }
}
