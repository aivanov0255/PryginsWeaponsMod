package com.prygin.menu;

import com.prygin.Guns;
import com.prygin.block.RechargerContainerMenu;
import com.prygin.item.ShotgunChamberMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final MenuType<RechargerContainerMenu> RECHARGER = register("recharger", RechargerContainerMenu::new);

    public static final MenuType<ShotgunChamberMenu> SHOTGUN_CHAMBER = register("shotgun_chamber", ShotgunChamberMenu::new);

    public static final MenuType<AmmoBenchMenu> AMMO_BENCH_MENU = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "ammo_bench"),
            new MenuType<>(AmmoBenchMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static <T extends AbstractContainerMenu> MenuType<T> register(
            String name,
            MenuType.MenuSupplier<T> constructor
    ) {
        return Registry.register(BuiltInRegistries.MENU, name, new MenuType<>(constructor, FeatureFlagSet.of()));
    }

    public static void init() {}
}
