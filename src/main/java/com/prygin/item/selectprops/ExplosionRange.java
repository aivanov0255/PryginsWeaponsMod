package com.prygin.item.selectprops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.prygin.item.components.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ExplosionRange() implements RangeSelectItemModelProperty {
    public static final MapCodec<ExplosionRange> MAP_CODEC = MapCodec.unit(new ExplosionRange());
    @Override
    public float get(final ItemStack itemStack, @Nullable final ClientLevel level, @Nullable final ItemOwner owner, final int seed) {
        return itemStack.get(ModComponents.SHOTGUN_AMMO_PROPERTIES).explodeRange();
    }

    @Override
    public MapCodec<ExplosionRange> type() {
        return MAP_CODEC;
    }
}
