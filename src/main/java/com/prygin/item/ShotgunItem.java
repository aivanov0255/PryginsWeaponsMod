package com.prygin.item;

import com.prygin.Guns;
import com.prygin.item.components.ModComponents;
import com.prygin.item.components.ShotgunAmmoProperties;
import com.prygin.item.components.ShotgunChamberTooltip;
import com.prygin.menu.ModMenuTypes;
import com.prygin.screenshake.ScreenShakePayload;
import net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.client.rendering.AtlasRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ShotgunItem extends GunItem implements PoseHoldable{

    public ShotgunItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (owner instanceof Player player
                && isUsing(owner)
                && Objects.equals(owner.getWeaponItem(), itemStack)) {

            if (player.containerMenu instanceof ShotgunChamberMenu) {
                return;
            }

            if (!player.getCooldowns().isOnCooldown(itemStack)) {
                player.getCooldowns().addCooldown(itemStack, gunProperties.cooldown());
                shoot(itemStack, level, owner);
            }
        }

        super.inventoryTickItem(itemStack, level, owner, slot);
    }

    public void shoot(ItemStack itemStack, ServerLevel level, Entity owner) {
        if (owner.isCrouching()) {
            if (!(owner instanceof Player player)) {
                return;
            }

            player.stopUsingItem();

            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("menu.shotgun_chamber");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    return new ShotgunChamberMenu(containerId, inventory, itemStack);
                }
            });
            return;
        }

        float spreadSpacing = 0.05f;

        Vec3 look = owner.getLookAngle();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        if (right.lengthSqr() == 0) {
            right = new Vec3(1, 0, 0);
        }
        Vec3 up = right.cross(look).normalize();

        List<ItemStack> inv = itemStack.getOrDefault(ModComponents.SHOTGUN_CHAMBER, List.of());
        int shotsFired = inv.size();

        boolean played = false;

        Map<UUID, Integer> damageMap = new HashMap<>();

        for (int i = 0; i < shotsFired; i++) {
            ItemStack currentItem = inv.get(i);
            ShotgunAmmoProperties shotgunAmmoProperties = currentItem.get(ModComponents.SHOTGUN_AMMO_PROPERTIES);
            if (shotgunAmmoProperties != null) {
                if (!played) {
                    level.playSound(null, owner, shootSound, SoundSource.PLAYERS, 1.0f, 1.0f);
                    played = true;
                }
                int gridX = i % 4;
                int gridY = i / 4;

                double offsetX = (gridX - 1.5) * spreadSpacing;
                double offsetY = (gridY - 1.5) * spreadSpacing;

                Vec3 modifiedLook = look.add(right.scale(offsetX)).add(up.scale(offsetY)).normalize();

                Vec3 startPos = owner.getEyePosition();
                Vec3 endPos = startPos.add(modifiedLook.scale(gunProperties.range()));

                HitResult hit = Guns.raycast(level, startPos, endPos, owner, gunProperties.breakBlocks());

                if (hit instanceof EntityHitResult entityHitResult) {
                    UUID uuid = entityHitResult.getEntity().getUUID();

                    if (damageMap.containsKey(uuid)) {
                        damageMap.replace(uuid, damageMap.get(uuid) + shotgunAmmoProperties.hitDamage());
                    } else {
                        damageMap.put(uuid, shotgunAmmoProperties.hitDamage());
                    }

                }

                if (shotgunAmmoProperties.explodeRange() > 0) {
                    Vec3 loc = hit.getLocation();
                    level.explode(owner, loc.x, loc.y, loc.z, shotgunAmmoProperties.explodeRange(), Level.ExplosionInteraction.BLOCK);
                }

                if (shotgunAmmoProperties.teleportTargetRandomly() && hit instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity) {
                    LivingEntity user = (LivingEntity) entityHitResult.getEntity();

                    boolean teleported = false;

                    for (int attempt = 0; attempt < 16; attempt++) {
                        double xx = user.getX() + (user.getRandom().nextDouble() - 0.5) * 16;
                        double yy = Mth.clamp(
                                user.getY() + (user.getRandom().nextDouble() - 0.5) * 16,
                                (double)level.getMinY(),
                                (double)(level.getMinY() + ((ServerLevel)level).getLogicalHeight() - 1)
                        );
                        double zz = user.getZ() + (user.getRandom().nextDouble() - 0.5) * 16;
                        if (user.isPassenger()) {
                            user.stopRiding();
                        }

                        Vec3 oldPos = user.position();
                        if (user.randomTeleport(xx, yy, zz, true)) {
                            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(user));
                            SoundSource soundSource;
                            SoundEvent soundEvent;
                            if (user instanceof Fox) {
                                soundEvent = SoundEvents.FOX_TELEPORT;
                                soundSource = SoundSource.NEUTRAL;
                            } else {
                                soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                                soundSource = SoundSource.PLAYERS;
                            }

                            level.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundSource);
                            user.resetFallDistance();
                            teleported = true;
                            break;
                        }
                    }

                    if (teleported) {
                        user.resetCurrentImpulseContext();
                    }
                }

                if (shotgunAmmoProperties.statusEffect().isEmpty() && hit instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                    List<MobEffectInstance> effects = shotgunAmmoProperties.statusEffect().get();

                    for (MobEffectInstance effect : effects) livingEntity.addEffect(effect);
                }

                ClientboundShootPayload payload = new ClientboundShootPayload(gunProperties, owner.getUUID(), hit);

                for (ServerPlayer serverPlayer : PlayerLookup.level(level)) {
                    ServerPlayNetworking.send(serverPlayer, payload);
                }

                if (owner instanceof Player player) {
                    ServerPlayNetworking.send((ServerPlayer) player, new ScreenShakePayload(shakeIntensity, shakeDuration));
                }
            }
        }

        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack round : inv) {
            ItemStack copy = round.copy();
            if (copy.get(ModComponents.SHOTGUN_AMMO_PROPERTIES) != null) {
                copy.shrink(1);
            }
            if (!copy.isEmpty()) {
                remaining.add(copy);
            }
        }

        for (UUID id : damageMap.keySet()) {
            Entity entity = level.getEntity(id);
            if (entity == null || !entity.isAlive()) continue;
            entity.hurtServer(level, level.damageSources().mobAttack(asLivingOwner(owner)), damageMap.get(id));
        }

        itemStack.set(ModComponents.SHOTGUN_CHAMBER, remaining);
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

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        List<ItemStack> chamber = stack.get(ModComponents.SHOTGUN_CHAMBER);
        if (chamber == null) {
            return super.getTooltipImage(stack);
        }
        return Optional.of(new ShotgunChamberTooltip(chamber));
    }
}
