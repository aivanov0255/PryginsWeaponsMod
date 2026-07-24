package com.prygin.item.components;

import com.mojang.serialization.Codec;
import com.prygin.Guns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public class ModComponents {
    public static final DataComponentType<Integer> AMMO = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "ammo"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build()
    );

    public static final DataComponentType<Integer> FUSE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "fuse"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build()
    );

    public static final DataComponentType<List<ItemStack>> SHOTGUN_CHAMBER = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "shotgun_chamber"),
            DataComponentType.<List<ItemStack>>builder()
                    .persistent(ItemStack.CODEC.listOf())
                    .networkSynchronized(ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()))
                    .build()
    );

    public static final DataComponentType<ShotgunAmmoProperties> SHOTGUN_AMMO_PROPERTIES = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "shotgun_ammo_properties"),
            DataComponentType.<ShotgunAmmoProperties>builder()
                    .persistent(ShotgunAmmoProperties.CODEC)
                    .networkSynchronized(ShotgunAmmoProperties.STREAM_CODEC)
                    .build()
    );

    public static final DataComponentType<List<BlockPos>> DETONATOR_POSITIONS = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "detonator_positions"),
            DataComponentType.<List<BlockPos>>builder()
                    .persistent(BlockPos.CODEC.listOf())
                    .networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()))
                    .build()
    );

    protected static void initialize() {
        Guns.LOGGER.info("Registering {} components", Guns.MOD_ID);
    }
}
