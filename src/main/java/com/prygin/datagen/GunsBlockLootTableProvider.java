package com.prygin.datagen;

import com.prygin.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class GunsBlockLootTableProvider extends FabricBlockLootSubProvider {
    public GunsBlockLootTableProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.TITANIUM_BLOCK);
        dropSelf(ModBlocks.RECHARGER);
        dropSelf(ModBlocks.ACACIA_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.BIRCH_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.CRIMSON_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.CHERRY_FLOOR_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.DARK_OAK_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.JUNGLE_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.MANGROVE_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.OAK_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.PALE_OAK_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.SPRUCE_FLOOR_BOARD_BLOCK);
        dropSelf(ModBlocks.WARPED_FLOOR_BOARD_BLOCK);
    }
}
