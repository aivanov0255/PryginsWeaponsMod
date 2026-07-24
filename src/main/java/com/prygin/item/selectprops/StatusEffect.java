package com.prygin.item.selectprops;

import com.mojang.serialization.MapCodec;
import com.prygin.item.components.ModComponents;
import com.prygin.item.components.ShotgunAmmoProperties;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class StatusEffect implements ItemTintSource {
    public static MapCodec<StatusEffect> MAP_CODEC = MapCodec.unit(new StatusEffect());

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        ShotgunAmmoProperties properties = itemStack.get(ModComponents.SHOTGUN_AMMO_PROPERTIES);

        if (properties != null && properties.statusEffect().isPresent()) {
            List<MobEffectInstance> effectInstances = properties.statusEffect().get();

            if (effectInstances.isEmpty()) {
                return 0xFFFFFFFF;
            }

            int totalRed = 0;
            int totalGreen = 0;
            int totalBlue = 0;

            for (MobEffectInstance effectInstance : effectInstances) {
                Holder<MobEffect> effectHolder = effectInstance.getEffect();
                int rgbColor = effectHolder.value().getColor();

                totalRed += (rgbColor >> 16) & 0xFF;
                totalGreen += (rgbColor >> 8) & 0xFF;
                totalBlue += rgbColor & 0xFF;
            }

            int count = effectInstances.size();
            int avgRed = totalRed / count;
            int avgGreen = totalGreen / count;
            int avgBlue = totalBlue / count;

            return 0xFF000000 | (avgRed << 16) | (avgGreen << 8) | avgBlue;
        }

        return 0xFFFFFFFF;
    }

    @Override
    public MapCodec<StatusEffect> type() {
        return MAP_CODEC;
    }
}
