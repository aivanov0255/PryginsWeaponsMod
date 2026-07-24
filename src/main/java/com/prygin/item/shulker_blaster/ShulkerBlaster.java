package com.prygin.item.shulker_blaster;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.prygin.Guns;
import com.prygin.item.GunItem;
import com.prygin.item.components.ModComponents;
import com.prygin.sounds.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ShulkerBlaster extends GunItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final String IDLE_CONTROLLER_NAME = "idle_controller";
    public static final String SHOOT_CONTROLLER_NAME = "shoot_controller";

    public static final RawAnimation DISAPPEAR = RawAnimation.begin().then("dissappear", LoopType.PLAY_ONCE);
    public static final RawAnimation REAPPEAR = RawAnimation.begin().then("reappear", LoopType.PLAY_ONCE);
    public static final RawAnimation DISAPPEAR_REAPPEAR = RawAnimation.begin().then("dis_reappear", LoopType.PLAY_ONCE);
    public static final RawAnimation IDLE_LOADED = RawAnimation.begin().then("idle", LoopType.LOOP);
    public static final RawAnimation IDLE_EMPTY = RawAnimation.begin().then("empty", LoopType.LOOP);

    public static final Map<Long, Integer> AMMO_CACHE = new ConcurrentHashMap<>();

    public ShulkerBlaster(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);

        Integer ammo = itemStack.get(ModComponents.AMMO);

        if (ammo != null) {
            long instanceId = GeoItem.getOrAssignId(itemStack, level);
            Integer previous = AMMO_CACHE.put(instanceId, ammo);

            if (previous == null || !previous.equals(ammo)) {
                syncAmmoToClients(level, instanceId, ammo);
            }
        }
    }

    @Override
    public void shoot(ItemStack itemStack, ServerLevel level, Entity owner) {
        level.playSound(null, new BlockPos(owner.getBlockX(), owner.getBlockY(), owner.getBlockZ()), ModSounds.ROCKET_LAUNCHER_SHOOT, SoundSource.PLAYERS);

        Vec3 startPos = owner.getEyePosition();
        Vec3 endPos = owner.getEyePosition().add(owner.getLookAngle().scale(gunProperties.range()));

        HitResult hit = Guns.raycast(level, startPos, endPos, owner, new ArrayList<>());

        ShulkerBullet bullet;

        if (hit instanceof EntityHitResult entityHitResult) bullet = new ShulkerBullet(level, asLivingOwner(owner), entityHitResult.getEntity(), null);
        else {
            bullet = new ShulkerBullet(level, asLivingOwner(owner), null, null);

            bullet.snapTo(owner.getEyePosition());

            float pitch = (float) Math.toRadians(owner.getXRot());
            float yaw = (float) Math.toRadians(owner.getYRot());

            double x = -Math.sin(yaw) * Math.cos(pitch);
            double y = -Math.sin(pitch);
            double z = Math.cos(yaw) * Math.cos(pitch);

            double speed = 1;
            Vec3 velocity = owner.getDeltaMovement().add(x * speed, y * speed, z * speed);

            bullet.setDeltaMovement(velocity);

            bullet.setXRot(-owner.getXRot());
            bullet.setYRot(-owner.getYRot());
        }

        level.addFreshEntity(bullet);

        Integer ammo = itemStack.get(ModComponents.AMMO);
        long instanceId = GeoItem.getOrAssignId(itemStack, level);

        int ammoValue = ammo == null ? 0 : ammo;
        AMMO_CACHE.put(instanceId, ammoValue);
        syncAmmoToClients(level, instanceId, ammoValue);

        if (ammo != null) {
            if (ammo == 0) {
                this.triggerAnim(owner, instanceId, SHOOT_CONTROLLER_NAME, "dissappear");
            } else {
                this.triggerAnim(owner, instanceId, SHOOT_CONTROLLER_NAME, "dis_reappear");
            }
        }
    }

    private static void syncAmmoToClients(ServerLevel level, long instanceId, int ammo) {
        AmmoSyncPayload payload = new AmmoSyncPayload(instanceId, ammo);

        for (ServerPlayer serverPlayer : PlayerLookup.level(level)) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

        AnimationController.AnimationStateHandler idlePredicate = (AnimationController.AnimationStateHandler<ShulkerBlaster>) state -> {
            Long instanceId = state.getData(DataTickets.ANIMATABLE_INSTANCE_ID);

            if (instanceId == null) {
                return PlayState.STOP;
            }

            Integer ammo = AMMO_CACHE.get(instanceId);

            if (ammo != null && ammo != 0) {
                return state.setAndContinue(IDLE_LOADED);
            }

            return state.setAndContinue(IDLE_EMPTY);
        };

        controllerRegistrar.add(new AnimationController<>(IDLE_CONTROLLER_NAME, 5, idlePredicate));

        controllerRegistrar.add(new AnimationController<ShulkerBlaster>(SHOOT_CONTROLLER_NAME, 5, state -> PlayState.STOP)
                .triggerableAnim("dissappear", DISAPPEAR)
                .triggerableAnim("dis_reappear", DISAPPEAR_REAPPEAR)
                .triggerableAnim("reappear", REAPPEAR));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final ShulkerBlasterRenderer renderer = new ShulkerBlasterRenderer();

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}