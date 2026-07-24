package com.prygin.datagen;

import com.prygin.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RECHARGER.properties().blockId())
                .add(ModBlocks.RECHARGER.properties().blockId());

        builder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.RECHARGER.properties().blockId())
                .add(ModBlocks.TITANIUM_BLOCK.properties().blockId());
    }
}
