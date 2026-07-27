package com.prygin.entity;

import com.prygin.Guns;
import com.prygin.entity.battle_axe.BattleAxeEntity;
import com.prygin.entity.boomerang.BoomerangEntity;
import com.prygin.entity.hook.HookEntity;
import com.prygin.entity.missle.MissleEntity;
import com.prygin.entity.nijastar.NinjaStarEntity;
import com.prygin.entity.plane.Plane;
import com.prygin.entity.scythe_zombie.ScytheZombie;
import com.prygin.entity.shockwave.ShockwaveEntity;
import com.prygin.item.ModItems;
import com.prygin.trap.EffectTrap;
import com.prygin.trap.MobTrap;
import com.prygin.trap.SpikeTrap;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class ModEntityTypes {
    public static final EntityType<@NotNull ScytheZombie> SCYTHE_ZOMBIE = register(
            "scythe_zombie",
            EntityType.Builder.of(ScytheZombie::new, MobCategory.MONSTER)
    );

    public static final EntityType<@NotNull ShockwaveEntity> SHOCKWAVE = register(
            "shockwave",
            EntityType.Builder.of(ShockwaveEntity::new, MobCategory.AMBIENT)
    );

    public static final EntityType<@NotNull NinjaStarEntity> NINJA_STAR = register(
            "ninja_star",
            EntityType.Builder.<NinjaStarEntity>of(NinjaStarEntity::new, MobCategory.AMBIENT).sized(0.3f, 0.3f)
    );

    public static final EntityType<@NotNull BattleAxeEntity> BATTLE_AXE = register(
            "battle_axe",
            EntityType.Builder.of(BattleAxeEntity::new, MobCategory.AMBIENT)
    );

    public static final EntityType<SpikeTrap> SPIKE_TRAP = register(
            "spike_trap",
            EntityType.Builder.of(SpikeTrap::new, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<EffectTrap> SLOWNESS_TRAP = register(
            "slowness_trap",
            EntityType.Builder.<EffectTrap>of(
                    (type, level) -> new EffectTrap(type, level, MobEffects.SLOWNESS, 160, 2, true, true, true) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.SLOWNESS_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<EffectTrap> POISON_TRAP = register(
            "poison_trap",
            EntityType.Builder.<EffectTrap>of(
                    (type, level) -> new EffectTrap(type, level, MobEffects.POISON, 160, 2, true, true, true) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.POISON_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<EffectTrap> BLINDNESS_TRAP = register(
            "blindness_trap",
            EntityType.Builder.<EffectTrap>of(
                    (type, level) -> new EffectTrap(type, level, MobEffects.BLINDNESS, 160, 2, true, true, true) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.BLINDNESS_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<EffectTrap> GLOWING_TRAP = register(
            "glowing_trap",
            EntityType.Builder.<EffectTrap>of(
                    (type, level) -> new EffectTrap(type, level, MobEffects.GLOWING, 160, 2, true, true, true) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.GLOWING_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<MobTrap> CREEPER_TRAP = register(
            "creeper_trap",
            EntityType.Builder.<MobTrap>of(
                    (type, level) -> new MobTrap(type, level, EntityTypes.CREEPER, 3, true) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.CREEPER_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<MobTrap> SILVERFISH_TRAP = register(
            "silverfish_trap",
            EntityType.Builder.<MobTrap>of(
                    (type, level) -> new MobTrap(type, level, EntityTypes.SILVERFISH, 10, false) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.SILVERFISH_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<MobTrap> TNT_TRAP = register(
            "tnt_trap",
            EntityType.Builder.<MobTrap>of(
                    (type, level) -> new MobTrap(type, level, EntityTypes.TNT, 10, false) {
                        @Override
                        public Item getTrapItem() {
                            return ModItems.TNT_TRAP;
                        }
                    }, MobCategory.AMBIENT).sized(1, 0.5f)
    );

    public static final EntityType<Granade> GRANADE = register(
            "granade",
            EntityType.Builder.<Granade>of(Granade::new, MobCategory.AMBIENT).sized(0.4f, 0.4f).clientTrackingRange(128)
    );

    public static final EntityType<MissleEntity> MISSLE = register(
            "missle",
            EntityType.Builder.<MissleEntity>of(MissleEntity::new, MobCategory.AMBIENT).sized(1f, 1f)
    );

    public static final EntityType<HookEntity> HOOK = register(
            "hook",
            EntityType.Builder.<HookEntity>of(HookEntity::new, MobCategory.AMBIENT).sized(0.4f, 0.4f)
    );

    public static final EntityType<Plane> PLANE = register(
            "plane",
            EntityType.Builder.<Plane>of(Plane::new, MobCategory.MISC)
                    .sized(5, 5)
                    .clientTrackingRange(10)
                    .updateInterval(1)
    );

    public static final EntityType<BoomerangEntity> BOOMERANG = register(
            "boomerang",
            EntityType.Builder.<BoomerangEntity>of(BoomerangEntity::new, MobCategory.MISC)
                    .sized(1, 1)
                    .updateInterval(1)
                    .clientTrackingRange(64)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Guns.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void registerModEntityTypes() {
        Guns.LOGGER.info("Registering EntityTypes for " + Guns.MOD_ID);
    }

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(SCYTHE_ZOMBIE, ScytheZombie.createAttributes());
    }
}
