package com.prygin.item;

import com.prygin.block.ModBlocks;
import com.prygin.block.RangedDetonator;
import com.prygin.item.components.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DetonatorItem extends Item {
    public DetonatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);

        List<BlockPos> positions = itemStack.get(ModComponents.DETONATOR_POSITIONS);
        if (positions.isEmpty()) return;
        List<BlockPos> mutablePositions = new ArrayList<>(positions);

        mutablePositions.removeIf(pos -> !(level.getBlockState(pos).getBlock() instanceof RangedDetonator));

        itemStack.set(ModComponents.DETONATOR_POSITIONS, mutablePositions);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getBlockStateOn().getBlock() instanceof RangedDetonator) return InteractionResult.PASS;

        if (player.isCrouching()) {
            ItemStack itemStack = player.getItemInHand(hand);
            List<BlockPos> positions = itemStack.get(ModComponents.DETONATOR_POSITIONS);

            if (positions.isEmpty()) return InteractionResult.PASS;

            for (BlockPos pos : positions) {
                if (!(level.getBlockState(pos).getBlock() instanceof RangedDetonator)) continue;
                level.explode(player, pos.getX(), pos.getY(), pos.getZ(), 5, Level.ExplosionInteraction.BLOCK);
            }

            itemStack.set(ModComponents.DETONATOR_POSITIONS, new ArrayList<>());

            player.getCooldowns().addCooldown(itemStack, 100);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        List<BlockPos> positions = itemStack.get(ModComponents.DETONATOR_POSITIONS);

        if (positions.isEmpty()) return;

        builder.accept(Component.translatable("tooltip.guns.binded_position").withColor(TextColor.BLUE));

        for (BlockPos pos : positions) {
            builder.accept(Component.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ()).withColor(TextColor.YELLOW));
        }
    }
}
