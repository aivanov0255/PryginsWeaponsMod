package com.prygin.item.components;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ShotgunChamberTooltip(List<ItemStack> shells) implements TooltipComponent {
}