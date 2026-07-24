package com.prygin.item.minigun;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.prygin.item.GunItem;
import com.prygin.item.components.ModComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class MinigunItem extends GunItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation SHOOT_ANIM = RawAnimation.begin().then("shoot", LoopType.LOOP);

    public MinigunItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
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

        super.inventoryTickItem(itemStack, level, owner, slot);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final MinigunRenderer renderer = new MinigunRenderer();

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>("shooting", 0, state -> {
                    if (Minecraft.getInstance().options.keyUse.isDown()) {
                        assert Minecraft.getInstance().player != null;
                        if (Minecraft.getInstance().player.getWeaponItem().getItem() instanceof MinigunItem) {
                            return state.setAndContinue(SHOOT_ANIM);
                        }
                    }

                    state.controller().reset();
                    return PlayState.STOP;
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
