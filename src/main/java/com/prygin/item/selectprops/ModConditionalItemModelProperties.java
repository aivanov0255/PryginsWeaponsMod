package com.prygin.item.selectprops;

import com.mojang.serialization.MapCodec;
import com.prygin.Guns;
import com.prygin.mixin.ConditionalItemModelPropertiesAccessor;
import net.minecraft.client.renderer.item.properties.conditional.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public class ModConditionalItemModelProperties {
    public static void bootstrap() {
        var mapper = ConditionalItemModelPropertiesAccessor.getIdMapper();
        mapper.put(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "status_effect_exists"), StatusEffectExists.MAP_CODEC);
        mapper.put(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "has_hook"), HasHook.MAP_CODEC);
        mapper.put(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "teleport_random"), TeleportRandom.MAP_CODEC);
    }
}
