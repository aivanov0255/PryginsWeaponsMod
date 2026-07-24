package com.prygin;

import com.prygin.datagen.GunsBlockLootTableProvider;
import com.prygin.datagen.GunsEnglishLangProvider;
import com.prygin.datagen.GunsModelProvider;
import com.prygin.datagen.ModBlockTagsProvider;
import com.prygin.item.selectprops.ModConditionalItemModelProperties;
import com.prygin.item.selectprops.ModItemTintSources;
import com.prygin.item.selectprops.ModRangeSelectItemModelProperties;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GunsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		ModConditionalItemModelProperties.bootstrap();
		ModItemTintSources.bootstrap();
		ModRangeSelectItemModelProperties.bootstrap();

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(GunsEnglishLangProvider::new);
		pack.addProvider(GunsModelProvider::new);
		pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider(GunsBlockLootTableProvider::new);
	}
}
