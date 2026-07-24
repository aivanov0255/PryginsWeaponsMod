package com.prygin.item;

import com.prygin.item.components.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class WatergunItem extends GunItem{
    public WatergunItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);
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
