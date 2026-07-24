package com.prygin.item.selectprops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.prygin.Guns;
import com.prygin.mixin.ItemTintSourcesAccessor;
import net.minecraft.client.color.item.*;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public class ModItemTintSources {
    public static void bootstrap() {
        ItemTintSourcesAccessor.getIdMapper()
                .put(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "status_effect"), StatusEffect.MAP_CODEC);
    }
}
