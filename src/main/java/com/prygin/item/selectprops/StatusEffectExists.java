package com.prygin.item.selectprops;

import com.mojang.serialization.MapCodec;
import com.prygin.item.components.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public record StatusEffectExists() implements ConditionalItemModelProperty {
    public static MapCodec<StatusEffectExists> MAP_CODEC = MapCodec.unit(new StatusEffectExists());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        Optional<List<MobEffectInstance>> effectInstances = itemStack.get(ModComponents.SHOTGUN_AMMO_PROPERTIES).statusEffect();

        return effectInstances != null && effectInstances.isPresent();
    }

    @Override
    public MapCodec<StatusEffectExists> type() {
        return MAP_CODEC;
    }
}
