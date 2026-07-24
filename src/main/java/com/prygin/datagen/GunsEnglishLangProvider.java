package com.prygin.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class GunsEnglishLangProvider extends FabricLanguageProvider {
    public GunsEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("item.guns.ammo.info", "%1$s ammo in chamber.");

        translationBuilder.add("item.guns.fuse.info", "%1$s seconds");

        translationBuilder.add("item.guns.pistol", "Pistol");
        translationBuilder.add("item.guns.pistol_ammo", "Pistol Ammo");

        translationBuilder.add("item.guns.shotgun", "Shotgun");
        translationBuilder.add("item.guns.shotgun_ammo", "Shotgun Shell");
        translationBuilder.add("item.guns.small_explosion_shell", "Small Explosion Shotgun Shell");
        translationBuilder.add("item.guns.medium_explosion_shell", "Medium Explosion Shotgun Shell");
        translationBuilder.add("item.guns.large_explosion_shell", "Large Explosion Shotgun Shell");
        translationBuilder.add("item.guns.chorus_shotgun_shell", "Chorus Shotgun Shell");
        translationBuilder.add("item.guns.poison_shotgun_shell", "Poisonous Shotgun Shell");
        translationBuilder.add("item.guns.slowness_shotgun_shell", "Slowing Shotgun Shell");
        translationBuilder.add("item.guns.weakness_shotgun_shell", "Weakening Shotgun Shell");
        translationBuilder.add("item.guns.blindness_shotgun_shell", "Blinding Shotgun Shell");

        translationBuilder.add("item.guns.mini_gun", "Minigun");
        translationBuilder.add("item.guns.minigun_ammo", "Minigun Ammo");

        translationBuilder.add("item.guns.watergun", "Water Gun");
        translationBuilder.add("item.guns.watergun_cartridge", "Water Gun Cartridge");

        translationBuilder.add("item.guns.sniper", "Sniper Rifle");
        translationBuilder.add("item.guns.sniper_ammo", "Sniper Ammo");

        translationBuilder.add("item.guns.rocket_launcher", "Rocket Launcher");
        translationBuilder.add("item.guns.missle", "Missile");

        translationBuilder.add("item.guns.shulker_blaster", "Shulker Blaster");
        translationBuilder.add("item.guns.shulker_bullet", "Shulker Bullet");

        translationBuilder.add("item.guns.taser", "Taser");

        translationBuilder.add("item.guns.zombie_scythe", "Zombie Scythe");
        translationBuilder.add("item.guns.cane", "Cane");

        translationBuilder.add("item.guns.ninja_star", "Ninja Star");
        translationBuilder.add("item.guns.battle_axe", "Battle Axe");

        translationBuilder.add("item.guns.cyber_cannon", "Cyber Cannon");

        translationBuilder.add("item.guns.spike_trap", "Spike Trap");
        translationBuilder.add("item.guns.slowness_trap", "Slowness Trap");
        translationBuilder.add("item.guns.poison_trap", "Poison Trap");
        translationBuilder.add("item.guns.blindness_trap", "Blindness Trap");
        translationBuilder.add("item.guns.glowing_trap", "Glowing Trap");

        translationBuilder.add("item.guns.creeper_trap", "Creeper Trap");
        translationBuilder.add("item.guns.silverfish_trap", "Silverfish Trap");
        translationBuilder.add("item.guns.tnt_trap", "TNT Trap");

        translationBuilder.add("item.guns.canned_food", "Canned Food");

        translationBuilder.add("item.guns.granade", "Grenade");
        translationBuilder.add("item.guns.detonator", "Detonator");

        translationBuilder.add("item.guns.breeze_basher", "Breeze Basher");

        translationBuilder.add("block.guns.titanium_block", "Block of Titanium");

        translationBuilder.add("block.guns.acacia_floor_board", "Acacia Floor Board");
        translationBuilder.add("block.guns.birch_floor_board", "Birch Floor Board");
        translationBuilder.add("block.guns.cherry_floor_board", "Cherry Floor Board");
        translationBuilder.add("block.guns.crimson_floor_board", "Crimson Floor Board");
        translationBuilder.add("block.guns.dark_oak_floor_board", "Dark Oak Floor Board");
        translationBuilder.add("block.guns.jungle_floor_board", "Jungle Floor Board");
        translationBuilder.add("block.guns.mangrove_floor_board", "Mangrove Floor Board");
        translationBuilder.add("block.guns.oak_floor_board", "Oak Floor Board");
        translationBuilder.add("block.guns.pale_oak_floor_board", "Pale Oak Floor Board");
        translationBuilder.add("block.guns.spruce_floor_board", "Spruce Floor Board");
        translationBuilder.add("block.guns.warped_floor_board", "Warped Floor Board");
        translationBuilder.add("block.guns.recharger", "Recharger");
        translationBuilder.add("block.guns.sky_day_block", "Fake Day Sky");
        translationBuilder.add("block.guns.sky_sunset_block", "Fake Sunset Sky");
        translationBuilder.add("block.guns.sky_night_block", "Fake Night Sky");
        translationBuilder.add("block.guns.ammo_bench", "Ammo Workbench");
        translationBuilder.add("block.guns.ranged_detonator", "Ranged Detonator");

        translationBuilder.add("creativeTab.guns", "Guns");
        translationBuilder.add("creativeTab.melee", "Melee");
        translationBuilder.add("creativeTab.ranged", "Ranged");
        translationBuilder.add("creativeTab.guns_building_blocks", "Building Blocks");
        translationBuilder.add("creativeTab.traps", "Traps");
        translationBuilder.add("creativeTab.misc", "Misc");

        translationBuilder.add("container.recharger", "Recharger");
        translationBuilder.add("container.ammobench", "Ammo Workbench");
        translationBuilder.add("menu.shotgun_chamber", "Shotgun");

        translationBuilder.add("tooltip.guns.explode_range", "Explosion Power: %1$s");
        translationBuilder.add("tooltip.guns.hit_damage", "Damage: %1$s");
        translationBuilder.add("tooltip.guns.teleport_target_randomly", "Teleports Target Randomly");
        translationBuilder.add("tooltip.guns.status_effect", "Potion Effects: ");
        translationBuilder.add("tooltip.guns.binded_position", "Binded Detonators:");
    }
}