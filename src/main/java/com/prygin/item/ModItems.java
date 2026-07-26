package com.prygin.item;

import com.prygin.Guns;
import com.prygin.entity.ModEntityTypes;
import com.prygin.item.components.ModComponents;
import com.prygin.item.components.ShotgunAmmoProperties;
import com.prygin.item.minigun.MinigunItem;
import com.prygin.item.shulker_blaster.ShulkerBlaster;
import com.prygin.sounds.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ModItems {
    private static final List<Block> DEFAULT_DESTROY_BLOCKS = new ArrayList<>();
    private static final List<Block> WATER_GUN_DESTROY_BLOCKS = new ArrayList<>();

    static {
        // Glass and Glass Panes
        DEFAULT_DESTROY_BLOCKS.add(Blocks.GLASS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.GLASS_PANE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TINTED_GLASS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.white());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.white());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.orange());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.orange());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.magenta());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.magenta());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.lightBlue());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.lightBlue());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.yellow());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.yellow());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.lime());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.lime());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.pink());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.pink());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.gray());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.gray());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.lightGray());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.lightGray());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.cyan());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.cyan());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.purple());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.purple());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.blue());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.blue());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.brown());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.brown());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.green());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.green());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.red());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.red());

        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS.black());
        DEFAULT_DESTROY_BLOCKS.add(Blocks.STAINED_GLASS_PANE.black());

        // Fragile Lights and Electronics
        DEFAULT_DESTROY_BLOCKS.add(Blocks.GLOWSTONE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SEA_LANTERN);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.OCHRE_FROGLIGHT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PEARLESCENT_FROGLIGHT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.VERDANT_FROGLIGHT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.REDSTONE_LAMP);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TORCH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SOUL_TORCH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.REDSTONE_TORCH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.LANTERN);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SOUL_LANTERN);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.END_ROD);

        // Grass, Plants, and Foliage
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SHORT_GRASS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TALL_GRASS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.FERN);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.LARGE_FERN);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.BUSH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.DEAD_BUSH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.VINE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.GLOW_LICHEN);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.MANGROVE_PROPAGULE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.AZALEA);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.FLOWERING_AZALEA);

        // Flowers (Small)
        DEFAULT_DESTROY_BLOCKS.add(Blocks.DANDELION);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.POPPY);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.BLUE_ORCHID);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.ALLIUM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.AZURE_BLUET);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.RED_TULIP);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.ORANGE_TULIP);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.WHITE_TULIP);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PINK_TULIP);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.OXEYE_DAISY);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CORNFLOWER);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.LILY_OF_THE_VALLEY);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.WITHER_ROSE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TORCHFLOWER);

        // Flowers (Tall)
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SUNFLOWER);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.LILAC);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.ROSE_BUSH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PEONY);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PITCHER_PLANT);

        // Agricultural Crops
        DEFAULT_DESTROY_BLOCKS.add(Blocks.WHEAT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CARROTS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.POTATOES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.BEETROOTS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.MELON_STEM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PUMPKIN_STEM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.ATTACHED_MELON_STEM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.ATTACHED_PUMPKIN_STEM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TORCHFLOWER_CROP);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PITCHER_CROP);

        // Wild Crops and Fungi
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SUGAR_CANE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SWEET_BERRY_BUSH);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CACTUS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.NETHER_WART);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.BROWN_MUSHROOM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.RED_MUSHROOM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CRIMSON_FUNGUS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.WARPED_FUNGUS);

        // Fragile Objects
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CHORUS_FLOWER);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CHORUS_PLANT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SPORE_BLOSSOM);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.BIG_DRIPLEAF);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SMALL_DRIPLEAF);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.LILY_PAD);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PINK_PETALS);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.DECORATED_POT);

        // Tree Leaves
        DEFAULT_DESTROY_BLOCKS.add(Blocks.OAK_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SPRUCE_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.BIRCH_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.JUNGLE_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.ACACIA_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.DARK_OAK_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.MANGROVE_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CHERRY_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.PALE_OAK_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.AZALEA_LEAVES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.FLOWERING_AZALEA_LEAVES);

        // Vines and Hanging Plants
        DEFAULT_DESTROY_BLOCKS.add(Blocks.VINE);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TWISTING_VINES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TWISTING_VINES_PLANT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.WEEPING_VINES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.WEEPING_VINES_PLANT);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CAVE_VINES);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.CAVE_VINES_PLANT);

        // Eggs
        DEFAULT_DESTROY_BLOCKS.add(Blocks.SNIFFER_EGG);
        DEFAULT_DESTROY_BLOCKS.add(Blocks.TURTLE_EGG);

        WATER_GUN_DESTROY_BLOCKS.add(Blocks.FIRE);
    }

    public static final GunItem SHOTGUN = register("shotgun", properties -> new ShotgunItem(properties, new GunItem.GunProperties(
            50.0f,
            16,
            10,
            5,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/hit_decal.png"),
            "default",
            "default",
            DEFAULT_DESTROY_BLOCKS
    ), ModSounds.PISTOL_SHOOT, 2.0f, 5), new Item.Properties()
            .component(DataComponents.MAX_STACK_SIZE, 1)
            .component(ModComponents.SHOTGUN_CHAMBER, new ArrayList<>()));

    public static final Item SHOTGUN_AMMO = register("shotgun_ammo", Item::new, new Item.Properties().stacksTo(16).component(ModComponents.SHOTGUN_AMMO_PROPERTIES, new ShotgunAmmoProperties(0, 5, false, Optional.empty())));

    public static final GunItem PISTOL = register("pistol", properties -> new GunItem(properties, new GunItem.GunProperties(
            50.0f,
            50,
            10,
            5,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/hit_decal.png"),
            "default",
            "default",
            DEFAULT_DESTROY_BLOCKS
    ), ModSounds.PISTOL_SHOOT, 2.0f, 5), new Item.Properties().component(ModComponents.AMMO, 50).component(DataComponents.MAX_STACK_SIZE, 1));

    public static final AmmoItem PISTOL_AMMO = register("pistol_ammo", properties -> new AmmoItem(properties, PISTOL, ModSounds.PISTOL_RELOAD), new Item.Properties());

    public static final GunItem MINIGUN = register("mini_gun", properties -> new MinigunItem(properties, new GunItem.GunProperties(
            50.0f,
            100,
            3,
            3,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/hit_decal.png"),
            "default",
            "default",

            DEFAULT_DESTROY_BLOCKS
    ), ModSounds.MINIGUN_SHOOT, 1.0f, 5), new Item.Properties().component(ModComponents.AMMO, 100).component(DataComponents.MAX_STACK_SIZE, 1));

    public static final AmmoItem MINIGUN_AMMO = register("minigun_ammo", properties -> new AmmoItem(properties, MINIGUN, ModSounds.PISTOL_RELOAD), new Item.Properties());

    public static final GunItem SNIPER = register("sniper", properties -> new SniperItem(properties, new GunItem.GunProperties(
            200.0f,
            5,
            50,
            3,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/hit_decal.png"),
            "default",
            "default",
            DEFAULT_DESTROY_BLOCKS
    ), ModSounds.PISTOL_SHOOT, 2.0f, 5), new Item.Properties()
            .component(ModComponents.AMMO, 5)
            .component(DataComponents.MAX_STACK_SIZE, 1));

    public static final AmmoItem SNIPER_AMMO = register("sniper_ammo", properties -> new AmmoItem(properties, SNIPER, ModSounds.PISTOL_RELOAD), new Item.Properties());

    public static final GunItem WATER_GUN = register(
            "watergun", properties -> new WatergunItem(properties, new GunItem.GunProperties(
                    50.0f,
                    100,
                    5,
                    1,
                    Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/watergun_decal.png"),
                    "slowness",
                    "default",
                    WATER_GUN_DESTROY_BLOCKS
            ), ModSounds.WATERGUN_SHOOT, 0, 0), new Item.Properties().component(ModComponents.AMMO, 100).component(DataComponents.MAX_STACK_SIZE, 1)
    );

    public static final AmmoItem WATER_GUN_CARTRIDGE = register("watergun_cartridge", properties ->
            new WatergunCartridgeItem(properties, WATER_GUN, ModSounds.WATERGUN_RELOAD),
                new Item.Properties().component(ModComponents.AMMO, 100).component(DataComponents.MAX_STACK_SIZE, 1));

    public static final GunItem CYBER_CANNON = register(
            "cyber_cannon", properties -> new CyberCannonItem(properties, new GunItem.GunProperties(
                    500.0f,
                    100,
                    300,
                    15,
                    Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/cyber_cannon_decal.png"),
                    "default",
                    "default",
                    DEFAULT_DESTROY_BLOCKS
            ), ModSounds.CYBER_CANNON_SHOOT, 10.0f, 20), new Item.Properties().component(ModComponents.AMMO, 100).component(DataComponents.MAX_STACK_SIZE, 1)
    );

    public static final Item ZOMBIE_SCYTHE = register(
            "zombie_scythe",
            ZombieScythe::new,
            new Item.Properties().sword(ToolMaterial.DIAMOND, 1f, 1f).hoe(ToolMaterial.IRON, 1f, 1f)
    );

    public static final CaneItem CANE = register(
            "cane",
            CaneItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item NINJA_STAR = register(
            "ninja_star",
            NinjaStarItem::new,
            new Item.Properties()
    );

    public static final Item BATTLE_AXE = register(
            "battle_axe",
            BattleAxeItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static TrapItem SPIKE_TRAP = register(
            "spike_trap",
            props -> new TrapItem(props, ModEntityTypes.SPIKE_TRAP),
            new Item.Properties()
    );

    public static TrapItem SLOWNESS_TRAP = register(
            "slowness_trap",
            props -> new EffectTrapItem(props, ModEntityTypes.SLOWNESS_TRAP, MobEffects.SLOWNESS),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "effect_trap"))
    );

    public static TrapItem POISON_TRAP = register(
            "poison_trap",
            props -> new EffectTrapItem(props, ModEntityTypes.POISON_TRAP, MobEffects.POISON),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "effect_trap"))
    );

    public static TrapItem BLINDNESS_TRAP = register(
            "blindness_trap",
            props -> new EffectTrapItem(props, ModEntityTypes.BLINDNESS_TRAP, MobEffects.BLINDNESS),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "effect_trap"))
    );

    public static TrapItem GLOWING_TRAP = register(
            "glowing_trap",
            props -> new EffectTrapItem(props, ModEntityTypes.GLOWING_TRAP, MobEffects.GLOWING),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "effect_trap"))
    );

    public static TrapItem CREEPER_TRAP = register(
            "creeper_trap",
            props -> new MobTrapItem(props, ModEntityTypes.CREEPER_TRAP, EntityTypes.CREEPER),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "mob_trap"))
    );

    public static TrapItem SILVERFISH_TRAP = register(
            "silverfish_trap",
            props -> new MobTrapItem(props, ModEntityTypes.SILVERFISH_TRAP, EntityTypes.SILVERFISH),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "mob_trap"))
    );

    public static TrapItem TNT_TRAP = register(
            "tnt_trap",
            props -> new MobTrapItem(props, ModEntityTypes.TNT_TRAP, EntityTypes.TNT),
            new Item.Properties().modelId(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "mob_trap"))
    );

    public static Item CANNED_FOOD = register(
            "canned_food",
            Item::new,
            new Item.Properties().food(new FoodProperties(5, 6, false))
    );

    public static Item GRANADE = register(
            "granade",
            GranadeItem::new,
            new Item.Properties().component(ModComponents.FUSE, 60).stacksTo(16)
    );

    public static Item DETONATOR = register(
            "detonator",
            DetonatorItem::new,
            new Item.Properties().component(ModComponents.DETONATOR_POSITIONS, new ArrayList<>()).stacksTo(1)
    );

    public static GunItem ROCKET_LAUNCHER = register(
            "rocket_launcher",
            properties -> new RocketLauncherItem(properties, SoundEvents.GENERIC_EXPLODE.value(), 3, 20),
            new Item.Properties().component(ModComponents.AMMO, 1).stacksTo(1)
    );

    public static AmmoItem MISSLE = register(
            "missle",
            properties -> new AmmoItem(properties, ROCKET_LAUNCHER, ModSounds.ROCKET_LAUNCHER_LOAD),
            new Item.Properties()
    );

    public static GunItem SHULKER_BLASTER = register(
            "shulker_blaster",
            properties -> new ShulkerBlaster(properties, new GunItem.GunProperties(
                    100,
                    25,
                    40,
                    0,
                    Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/cyber_cannon_decal.png"),
                    "default",
                    "default",
                    new ArrayList<>()
            ), SoundEvents.SHULKER_SHOOT, 0.1f, 2),
            new Item.Properties().component(ModComponents.AMMO, 25).stacksTo(1)
    );

    public static AmmoItem SHULKER_BULLET = register(
            "shulker_bullet",
            properties -> new AmmoItem(properties, SHULKER_BLASTER, SoundEvents.SHULKER_CLOSE),
            new Item.Properties()
    );

    public static Item BREEZE_BASHER = register(
            "breeze_basher",
            BreezeBasherItem::new,
            new Item.Properties().stacksTo(1)
                    .sword(ToolMaterial.DIAMOND, 8, -2.4f)
                    .attributes(
                            ItemAttributeModifiers.builder()
                                .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(Identifier.withDefaultNamespace("generic.attack_knockback"),
                                        3,
                                        AttributeModifier.Operation.ADD_VALUE),
                                        EquipmentSlotGroup.MAINHAND
                                )
                                .build()
                    )
    );

    public static Item TASER = register(
            "taser",
            TaserItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static Item LEAF_BLOWER = register(
            "leafblower",
            LeafBlower::new,
            new Item.Properties().stacksTo(1)
    );
    public static Item VACUUM = register(
            "vacuum",
            Vacuum::new,
            new Item.Properties().stacksTo(1)
    );

    public static Item BOOMERANG = register(
            "boomerang",
            BoomerangItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guns.MOD_ID, name));

        T item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        LoggerFactory.getLogger("guns").info("initializing items");
    }
}
