package com.prygin.block.block_entity;

import com.prygin.Guns;
import com.prygin.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static void init() {}

    public static final BlockEntityType<SkyDayBlockEntity> SKY_DAY_BLOCK_ENTITY =
            register("sky_day", SkyDayBlockEntity::new, ModBlocks.SKY_DAY_BLOCK);

    public static final BlockEntityType<SkyNightBlockEntity> SKY_NIGHT_BLOCK_ENTITY =
            register("sky_night", SkyNightBlockEntity::new, ModBlocks.SKY_NIGHT_BLOCK);

    public static final BlockEntityType<SkySunSetBlockEntity> SKY_SUNSET_BLOCK_ENTITY =
            register("sky_sunset", SkySunSetBlockEntity::new, ModBlocks.SKY_SUNSET_BLOCK);

    public static final BlockEntityType<RechargerBlockEntity> RECHARGER =
            register("recharger", RechargerBlockEntity::new, ModBlocks.RECHARGER);
    public static final BlockEntityType<BombBlockEntity> BOMB_BLOCK_ENTITY =
            register("bomb_block", BombBlockEntity::new, ModBlocks.BOMB_BLOCK);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(Guns.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }
}
