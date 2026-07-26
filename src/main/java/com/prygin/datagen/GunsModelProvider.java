package com.prygin.datagen;

import com.mojang.serialization.Codec;
import com.prygin.Guns;
import com.prygin.block.ModBlocks;
import com.prygin.item.ModItems;
import com.prygin.item.components.ModComponents;
import com.prygin.item.components.ShotgunAmmoProperties;
import com.prygin.item.selectprops.ExplosionRange;
import com.prygin.item.selectprops.StatusEffect;
import com.prygin.item.selectprops.StatusEffectExists;
import com.prygin.item.selectprops.TeleportRandom;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.Broken;
import net.minecraft.client.renderer.item.properties.numeric.Count;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class GunsModelProvider extends FabricModelProvider {
    public GunsModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerator) {
        blockModelGenerator.createTrivialCube(ModBlocks.TITANIUM_BLOCK);

        blockModelGenerator.createTrivialCube(ModBlocks.OAK_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.SPRUCE_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.DARK_OAK_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.ACACIA_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.MANGROVE_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.BIRCH_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.JUNGLE_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.PALE_OAK_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.CHERRY_FLOOR_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.CRIMSON_FLOOR_BOARD_BLOCK);
        blockModelGenerator.createTrivialCube(ModBlocks.WARPED_FLOOR_BOARD_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.PISTOL_AMMO, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.MINIGUN_AMMO, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.WATER_GUN_CARTRIDGE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.SNIPER_AMMO, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CANNED_FOOD, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.GRANADE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.DETONATOR, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.MISSLE, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(ModItems.BREEZE_BASHER, ModelTemplates.FLAT_HANDHELD_ITEM);

        ItemModel.Unbaked explodes1 = ItemModelUtils.plainModel(
                Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shotgun_overlay/explodes_1"));
        ItemModel.Unbaked explodes2 = ItemModelUtils.plainModel(
                Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shotgun_overlay/explodes_2"));
        ItemModel.Unbaked explodes3 = ItemModelUtils.plainModel(
                Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shotgun_overlay/explodes_3"));

        ItemModel.Unbaked teleportsRandom = ItemModelUtils.plainModel(
                Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shotgun_overlay/teleports_random"));

        Identifier effectInstanceModel = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shotgun_overlay/effect_instance");

        Identifier emptyModelId = Identifier.withDefaultNamespace("block/air");

        itemModelGenerator.itemModelOutput.accept(ModItems.SHOTGUN_AMMO, ItemModelUtils.composite(
                ItemModelUtils.plainModel(
                        itemModelGenerator.createFlatItemModel(ModItems.SHOTGUN_AMMO, ModelTemplates.FLAT_ITEM)
                ),
                ItemModelUtils.rangeSelect(
                        new ExplosionRange(),
                        List.of(
                                ItemModelUtils.override(ItemModelUtils.plainModel(emptyModelId), 0.0f),
                                ItemModelUtils.override(explodes1, 2.0f),
                                ItemModelUtils.override(explodes2, 5.0f),
                                ItemModelUtils.override(explodes3, 10.0f)
                        )
                ),
                ItemModelUtils.conditional(
                        new TeleportRandom(),
                        teleportsRandom,
                        ItemModelUtils.plainModel(emptyModelId)
                ),
                ItemModelUtils.conditional(
                        new StatusEffectExists(),
                        ItemModelUtils.tintedModel(effectInstanceModel, new StatusEffect()),
                        ItemModelUtils.plainModel(emptyModelId)
                )
        ));

        Identifier flatModelZombieScythe = itemModelGenerator.createFlatItemModel(ModItems.ZOMBIE_SCYTHE, ModelTemplates.FLAT_ITEM);

        Identifier handModelZombieScythe = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/zombie_scythe_hand");

        itemModelGenerator.itemModelOutput.accept(
                ModItems.ZOMBIE_SCYTHE,
                ItemModelUtils.select(
                        new DisplayContext(),
                        ItemModelUtils.plainModel(handModelZombieScythe),
                        ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.plainModel(flatModelZombieScythe)),
                        ItemModelUtils.when(ItemDisplayContext.FIXED, ItemModelUtils.plainModel(flatModelZombieScythe))
                )
        );

        Identifier flatModelCane = itemModelGenerator.createFlatItemModel(ModItems.CANE, ModelTemplates.FLAT_ITEM);

        Identifier handModelCane = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/cane_hand");

        itemModelGenerator.itemModelOutput.accept(
                ModItems.CANE,
                ItemModelUtils.select(
                        new DisplayContext(),
                        ItemModelUtils.plainModel(handModelCane),
                        ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.plainModel(flatModelCane)),
                        ItemModelUtils.when(ItemDisplayContext.FIXED, ItemModelUtils.plainModel(flatModelCane))
                )
        );

        Identifier flatModelNinjaStar = itemModelGenerator.createFlatItemModel(ModItems.NINJA_STAR, ModelTemplates.FLAT_ITEM);

        Identifier handModelNinjaStar = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/ninja_star_hand");

        itemModelGenerator.itemModelOutput.accept(
                ModItems.NINJA_STAR,
                ItemModelUtils.select(
                        new DisplayContext(),
                        ItemModelUtils.plainModel(handModelNinjaStar),
                        ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.plainModel(flatModelNinjaStar)),
                        ItemModelUtils.when(ItemDisplayContext.FIXED, ItemModelUtils.plainModel(flatModelNinjaStar))
                )
        );
    }

    @Override
    public String getName() {
        return "GunsModelProvider";
    }
}
