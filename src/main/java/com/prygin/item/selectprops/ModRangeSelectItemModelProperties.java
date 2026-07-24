package com.prygin.item.selectprops;

import com.prygin.Guns;
import com.prygin.mixin.RangeSelectItemModelPropertiesAccessor;
import net.minecraft.resources.Identifier;

public class ModRangeSelectItemModelProperties {
    public static void bootstrap() {
        RangeSelectItemModelPropertiesAccessor.getIdMapper()
                .put(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "explosion_range"), ExplosionRange.MAP_CODEC);
    }
}
