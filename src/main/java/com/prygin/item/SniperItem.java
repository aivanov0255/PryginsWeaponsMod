package com.prygin.item;

import com.prygin.Guns;
import com.prygin.item.components.ModComponents;
import com.prygin.screenshake.ScreenShakePayload;
import com.prygin.zoom.ZoomManager;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class SniperItem extends GunItem implements PoseHoldable {
    boolean isZoomed = false;

    public SniperItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        Integer ammo = itemStack.get(ModComponents.AMMO);

        if (ammo != null) {
            if (owner instanceof Player player && isUsing(owner) && !player.getCooldowns().isOnCooldown(itemStack) && Objects.equals(owner.getWeaponItem(), itemStack) && ammo > 0) {
                ((Player)owner).getCooldowns().addCooldown(itemStack, gunProperties.cooldown());
                itemStack.set(ModComponents.AMMO, ammo - 1);

                shoot(itemStack, level, owner);
            }
        }

        if (owner instanceof ServerPlayer player) {
            if (player.isCrouching() && Objects.equals(owner.getWeaponItem(), itemStack)) {
                ServerPlayNetworking.send(player, new ClientboundZoomPayload(0.05f));
            } else {
                ServerPlayNetworking.send(player, new ClientboundZoomPayload(1f));
            }
        }

        super.inventoryTickItem(itemStack, level, owner, slot);
    }

    public void applyServerSideHitEffects(ServerLevel level, Entity owner, HitResult hit) {
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.hurt(level.damageSources().mobAttack(asLivingOwner(owner)),
                    gunProperties.damage() + owner.distanceTo(entityHit.getEntity())/2);

            gunProperties.entityHit().accept(entityHit);
        }
    }

    public static LivingEntity asLivingOwner(Entity owner) {
        return owner instanceof LivingEntity livingOwner ? livingOwner : null;
    }

    @Override
    public boolean posesOppositeArmWhenFree() {
        return true;
    }

    @Override
    public void applyHandPose(ModelPart arm, HumanoidArm armSide, ModelPart otherArm, ItemStack otherStack,
                              InteractionHand hand, ItemStack stack) {

        boolean drivingBothArms = otherStack.isEmpty();

        ModelPart rightArm = armSide == HumanoidArm.RIGHT ? arm : otherArm;
        ModelPart leftArm = armSide == HumanoidArm.RIGHT ? otherArm : arm;

        float forwardPitch = -80.0F * Mth.DEG_TO_RAD;
        float inwardYaw = 12.0F * Mth.DEG_TO_RAD;
        float wristRoll = 6.0F * Mth.DEG_TO_RAD;

        rightArm.xRot = forwardPitch;
        rightArm.yRot = -inwardYaw;
        rightArm.zRot = wristRoll;
        rightArm.x = -3.5F;

        if (drivingBothArms) {
            leftArm.xRot = forwardPitch;
            leftArm.yRot = inwardYaw;
            leftArm.zRot = -wristRoll;
            leftArm.x = 3.5F;
        }
    }
}
