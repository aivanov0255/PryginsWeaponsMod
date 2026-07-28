package com.prygin.block;

import com.prygin.Guns;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class ModBlocks {

    public static Block TITANIUM_BLOCK = register("titanium_block", Block::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(1.5f, 6.0f).sound(SoundType.IRON), true);
    
    public static Block OAK_FLOOR_BOARD_BLOCK = register("oak_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block SPRUCE_FLOOR_BOARD_BLOCK = register("spruce_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block DARK_OAK_FLOOR_BOARD_BLOCK = register("dark_oak_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block ACACIA_FLOOR_BOARD_BLOCK = register("acacia_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block MANGROVE_FLOOR_BOARD_BLOCK = register("mangrove_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block BIRCH_FLOOR_BOARD_BLOCK = register("birch_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block JUNGLE_FLOOR_BOARD_BLOCK = register("jungle_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block PALE_OAK_FLOOR_BOARD_BLOCK = register("pale_oak_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block CHERRY_FLOOR_FLOOR_BOARD_BLOCK = register("cherry_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), true);
    public static Block CRIMSON_FLOOR_BOARD_BLOCK = register("crimson_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD), true);
    public static Block WARPED_FLOOR_BOARD_BLOCK = register("warped_floor_board", Block::new, BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD), true);

    public static Block BOMB_BLOCK = register("bomb_block", BombBlock::new, BlockBehaviour.Properties.of(), true);

    public static final Block SKY_NIGHT_BLOCK = register(
            "sky_night_block",
            SkyNightBlock::new,
            BlockBehaviour.Properties.of(),
            true
    );

    public static final Block SKY_DAY_BLOCK = register(
            "sky_day_block",
            SkyDayBlock::new,
            BlockBehaviour.Properties.of().lightLevel((state) -> 15),
            true
    );

    public static final Block SKY_SUNSET_BLOCK = register(
            "sky_sunset_block",
            SkySunSetBlock::new,
            BlockBehaviour.Properties.of().lightLevel((state) -> 5),
            true
    );

    public static final Block RECHARGER = register(
            "recharger",
            RechargerBlock::new,
            BlockBehaviour.Properties.of().noOcclusion().requiresCorrectToolForDrops().strength(1.5F, 6.0F),
            true
    );

    public static final Block AMMO_BENCH = register(
            "ammo_bench",
            AmmoBench::new,
            BlockBehaviour.Properties.of().noOcclusion()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5f)
                    .sound(SoundType.WOOD),
            true
    );

    public static final Block RANGED_DETONATOR = register(
            "ranged_detonator",
            RangedDetonator::new,
            BlockBehaviour.Properties.of().noCollision().noOcclusion()
                    .mapColor(MapColor.METAL)
                    .strength(0.6f)
                    .sound(SoundType.IRON),
            true
    );

    public static final Block POPUP_WALL = register(
            "popup_wall",
            PopupWallBlock::new,
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.IRON),
            true
    );

    public static final Block TEMPORARY_POPUP_WALL = register(
            "temporary_popup_wall",
            TemporaryPopupWall::new,
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.IRON),
            false
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties, boolean shouldRegisterItem) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(properties.setId(blockKey));

        if (shouldRegisterItem) {
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }



    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Guns.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guns.MOD_ID, name));
    }

    public static void initialize() {
    }
}