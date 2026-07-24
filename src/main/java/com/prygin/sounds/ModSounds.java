package com.prygin.sounds;

import com.prygin.Guns;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent PISTOL_SHOOT = registerSound("pistol_shoot");
    public static final SoundEvent PISTOL_RELOAD = registerSound("pistol_reload");
    public static final SoundEvent MINIGUN_SHOOT = registerSound("minigun_shoot");
    public static final SoundEvent WATERGUN_SHOOT = registerSound("watergun_shoot");
    public static final SoundEvent WATERGUN_RELOAD = registerSound("watergun_reload");
    public static final SoundEvent CYBER_CANNON_SHOOT = registerSound("cyber_cannon_shoot");
    public static final SoundEvent NINJA_STAR_HIT = registerSound("ninja_star_hit");
    public static final SoundEvent ROCKET_LAUNCHER_SHOOT = registerSound("rocket_launcher_shoot");
    public static final SoundEvent ROCKET_LAUNCHER_LOAD = registerSound("rocket_launcher_load");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Guns.MOD_ID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void initialize() {
        Guns.LOGGER.info("Registering " + Guns.MOD_ID + " Sounds");
    }
}
