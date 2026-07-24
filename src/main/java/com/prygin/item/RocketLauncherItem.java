package com.prygin.item;

import com.prygin.Guns;
import com.prygin.entity.missle.MissleEntity;
import com.prygin.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class RocketLauncherItem extends GunItem {
    public RocketLauncherItem(Properties properties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, new GunProperties(0, 1, 100, 0, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/effect/hit_decal.png"), "default", "default", new ArrayList<>()), shootSound, shakeIntensity, shakeDuration);
    }

    @Override
    public void shoot(ItemStack itemStack, ServerLevel level, Entity owner) {
        level.playSound(null, new BlockPos(owner.getBlockX(), owner.getBlockY(), owner.getBlockZ()), ModSounds.ROCKET_LAUNCHER_SHOOT, SoundSource.PLAYERS);

        MissleEntity missle = new MissleEntity(level, (Player)owner);

        float pitch = (float) Math.toRadians(owner.getXRot());
        float yaw = (float) Math.toRadians(owner.getYRot());

        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = -Math.sin(pitch);
        double z = Math.cos(yaw) * Math.cos(pitch);

        double speed = 2;
        Vec3 velocity = owner.getDeltaMovement().add(x * speed, y * speed, z * speed);

        missle.setDeltaMovement(velocity);

        missle.setXRot(-owner.getXRot());
        missle.setYRot(-owner.getYRot());

        level.addFreshEntity(missle);
    }
}
