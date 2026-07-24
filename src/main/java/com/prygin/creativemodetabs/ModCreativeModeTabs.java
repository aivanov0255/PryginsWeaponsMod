package com.prygin.creativemodetabs;

import com.prygin.Guns;
import com.prygin.block.ModBlocks;
import com.prygin.item.ModItems;
import com.prygin.item.components.ModComponents;
import com.prygin.item.components.ShotgunAmmoProperties;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class ModCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> GUNS_CREATIVE_TABS_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guns.MOD_ID, "guns")
    );

    public static final ResourceKey<CreativeModeTab> MELEE_CREATIVE_TABS_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guns.MOD_ID, "melee")
    );

    public static final ResourceKey<CreativeModeTab> RANGED_CREATIVE_TABS_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guns.MOD_ID, "ranged")
    );

    public static final ResourceKey<CreativeModeTab> GUNS_BUILDING_BLOCKS_TABS_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guns.MOD_ID, "building_blocks")
    );

    public static final ResourceKey<CreativeModeTab> TRAPS_TABS_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guns.MOD_ID, "traps")
    );

    public static final ResourceKey<CreativeModeTab> MISC_TABS_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guns.MOD_ID, "misc")
    );

    public static final CreativeModeTab GUNS_CREATIVE_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.PISTOL))
            .title(Component.translatable("creativeTab.guns"))
            .displayItems((params, output) -> {
                output.accept(ModItems.PISTOL);
                output.accept(ModItems.PISTOL_AMMO);

                output.accept(ModItems.MINIGUN);
                output.accept(ModItems.MINIGUN_AMMO);

                output.accept(ModItems.WATER_GUN);
                output.accept(ModItems.WATER_GUN_CARTRIDGE);

                output.accept(ModItems.CYBER_CANNON);
                output.accept(ModBlocks.RECHARGER);

                output.accept(ModItems.SHOTGUN);
                output.accept(ModItems.SHOTGUN_AMMO);
                output.accept(createShotgunAmmo(2, 5, false, Optional.empty(),
                        Component.translatable("item.guns.small_explosion_shell")));
                output.accept(createShotgunAmmo(5, 5, false, Optional.empty(),
                        Component.translatable("item.guns.medium_explosion_shell")));
                output.accept(createShotgunAmmo(10, 5, false, Optional.empty(),
                        Component.translatable("item.guns.large_explosion_shell")));
                output.accept(createShotgunAmmo(0, 5, true, Optional.empty(),
                        Component.translatable("item.guns.chorus_shotgun_shell")));

                output.accept(createShotgunAmmo(0, 5, false, new MobEffectInstance(MobEffects.POISON, 200, 5, false, true),
                        Component.translatable("item.guns.poison_shotgun_shell")));
                output.accept(createShotgunAmmo(0, 5, false, new MobEffectInstance(MobEffects.SLOWNESS, 200, 5, false, true),
                        Component.translatable("item.guns.slowness_shotgun_shell")));
                output.accept(createShotgunAmmo(0, 5, false, new MobEffectInstance(MobEffects.WEAKNESS, 200, 5, false, true),
                        Component.translatable("item.guns.weakness_shotgun_shell")));
                output.accept(createShotgunAmmo(0, 5, false, new MobEffectInstance(MobEffects.BLINDNESS, 200, 5, false, true),
                        Component.translatable("item.guns.blindness_shotgun_shell")));


                output.accept(ModItems.SNIPER);
                output.accept(ModItems.SNIPER_AMMO);

                output.accept(ModItems.ROCKET_LAUNCHER);
                output.accept(ModItems.MISSLE);

                output.accept(ModItems.SHULKER_BLASTER);
                output.accept(ModItems.SHULKER_BULLET);
            })
            .build();

    public static final CreativeModeTab MELEE_CREATIVE_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.ZOMBIE_SCYTHE))
            .title(Component.translatable("creativeTab.melee"))
            .displayItems((params, output) -> {
                output.accept(ModItems.ZOMBIE_SCYTHE);
                output.accept(ModItems.CANE);

                output.accept(ModItems.BREEZE_BASHER);

                output.accept(ModItems.TASER);
            })
            .build();

    public static final CreativeModeTab RANGED_CREATIVE_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.NINJA_STAR))
            .title(Component.translatable("creativeTab.ranged"))
            .displayItems((params, output) -> {
                output.accept(ModItems.NINJA_STAR);
                output.accept(ModItems.BATTLE_AXE);

                ItemStack granade = new ItemStack(ModItems.GRANADE);
                granade.set(ModComponents.FUSE, 30);
                output.accept(granade);

                ItemStack granade1 = new ItemStack(ModItems.GRANADE);
                granade1.set(ModComponents.FUSE, 60);
                output.accept(granade1);

                ItemStack granade2 = new ItemStack(ModItems.GRANADE);
                granade2.set(ModComponents.FUSE, 80);
                output.accept(granade2);

                ItemStack granade3 = new ItemStack(ModItems.GRANADE);
                granade3.set(ModComponents.FUSE, 100);
                output.accept(granade3);

                output.accept(ModBlocks.RANGED_DETONATOR);
                output.accept(ModItems.DETONATOR);
            })
            .build();

    public static final CreativeModeTab GUNS_BUILDING_BLOCKS_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.OAK_FLOOR_BOARD_BLOCK))
            .title(Component.translatable("creativeTab.guns_building_blocks"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.OAK_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.SPRUCE_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.DARK_OAK_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.ACACIA_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.MANGROVE_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.BIRCH_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.JUNGLE_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.PALE_OAK_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.CHERRY_FLOOR_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.CRIMSON_FLOOR_BOARD_BLOCK);
                output.accept(ModBlocks.WARPED_FLOOR_BOARD_BLOCK);

                output.accept(ModBlocks.TITANIUM_BLOCK);

                output.accept(ModBlocks.SKY_DAY_BLOCK);
                output.accept(ModBlocks.SKY_NIGHT_BLOCK);
                output.accept(ModBlocks.SKY_SUNSET_BLOCK);

                output.accept(ModBlocks.AMMO_BENCH);
            })
            .build();

    public static final CreativeModeTab TRAPS_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.SPIKE_TRAP))
            .title(Component.translatable("creativeTab.traps"))
            .displayItems((params, output) -> {
                output.accept(ModItems.SPIKE_TRAP);
                output.accept(ModItems.SLOWNESS_TRAP);
                output.accept(ModItems.POISON_TRAP);
                output.accept(ModItems.BLINDNESS_TRAP);
                output.accept(ModItems.GLOWING_TRAP);

                output.accept(ModItems.CREEPER_TRAP);
                output.accept(ModItems.SILVERFISH_TRAP);
                output.accept(ModItems.TNT_TRAP);
            })
            .build();

    public static final CreativeModeTab MISC_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.CANNED_FOOD))
            .title(Component.translatable("creativeTab.misc"))
            .displayItems((params, output) -> {
                output.accept(ModItems.CANNED_FOOD);
            })
            .build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, GUNS_CREATIVE_TABS_KEY, GUNS_CREATIVE_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MELEE_CREATIVE_TABS_KEY, MELEE_CREATIVE_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, RANGED_CREATIVE_TABS_KEY, RANGED_CREATIVE_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, GUNS_BUILDING_BLOCKS_TABS_KEY, GUNS_BUILDING_BLOCKS_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TRAPS_TABS_KEY, TRAPS_TAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MISC_TABS_KEY, MISC_TAB);
    }

    private static ItemStack createShotgunAmmo(int explodeRange, int hitDamage, boolean teleportTargetRandomly, Optional<List<MobEffectInstance>> statusEffect, Component name) {
        ItemStack stack = new ItemStack(ModItems.SHOTGUN_AMMO);

        stack.set(DataComponents.ITEM_NAME, name);

        stack.set(ModComponents.SHOTGUN_AMMO_PROPERTIES,
                new ShotgunAmmoProperties(explodeRange, hitDamage, teleportTargetRandomly, statusEffect));

        return stack;
    }

    private static ItemStack createShotgunAmmo(int explodeRange, int hitDamage, boolean teleportTargetRandomly, MobEffectInstance statusEffect, Component name) {
        ItemStack stack = new ItemStack(ModItems.SHOTGUN_AMMO);

        stack.set(DataComponents.ITEM_NAME, name);

        stack.set(ModComponents.SHOTGUN_AMMO_PROPERTIES,
                new ShotgunAmmoProperties(explodeRange, hitDamage, teleportTargetRandomly, Optional.of(List.of(statusEffect))));

        return stack;
    }
}
