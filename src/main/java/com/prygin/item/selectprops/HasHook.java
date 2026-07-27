package com.prygin.item.selectprops;

import com.mojang.serialization.MapCodec;
import com.prygin.item.components.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record HasHook() implements ConditionalItemModelProperty {
    public static MapCodec<HasHook> MAP_CODEC = MapCodec.unit(new HasHook());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return Boolean.TRUE.equals(itemStack.get(ModComponents.HAS_HOOK));
    }

    @Override
    public MapCodec<HasHook> type() {
        return MAP_CODEC;
    }
}
