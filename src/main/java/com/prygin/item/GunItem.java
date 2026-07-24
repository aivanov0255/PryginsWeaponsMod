package com.prygin.item;

import com.prygin.Guns;
import com.prygin.item.components.ModComponents;
import com.prygin.screenshake.ScreenShakePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class GunItem extends Item {
    public GunProperties gunProperties;

    SoundEvent shootSound;

    float shakeIntensity;
    int shakeDuration;

    public static final Map<String, Consumer<EntityHitResult>> ENTITY_HIT_CONSUMER_REGISTRY = new HashMap<>();
    public static final Map<String, Consumer<BlockHitResult>> BLOCK_HIT_CONSUMER_REGISTRY = new HashMap<>();

    static {
        ENTITY_HIT_CONSUMER_REGISTRY.put("default", hit -> {});
        BLOCK_HIT_CONSUMER_REGISTRY.put("default", hit -> {});

        ENTITY_HIT_CONSUMER_REGISTRY.put("slowness", hit -> {
            Entity e = hit.getEntity();

            if (e instanceof LivingEntity livingEntity) {
                MobEffectInstance effect = new MobEffectInstance(MobEffects.SLOWNESS, 200, 1, false, true);
                livingEntity.addEffect(effect);
            }
        });
    }

    private static <K, V> Map.Entry<K, V> getMapEntry(Map<K, V> map, K key) {
        V value = map.get(key);

        if (value != null || map.containsKey(key)) {
            return Map.entry(key, value);
        }

        return null;
    }

    public record GunProperties(float range, int maxAmmo, int cooldown, int damage,
                                Identifier decalImage,
                                String entityHitKey,
                                String blockHitKey,
                                List<Block> breakBlocks) {

        public Consumer<EntityHitResult> entityHit() {
            return ENTITY_HIT_CONSUMER_REGISTRY.getOrDefault(entityHitKey, hit -> {});
        }

        public Consumer<BlockHitResult> blockHit() {
            return BLOCK_HIT_CONSUMER_REGISTRY.getOrDefault(blockHitKey, hit -> {});
        }

        public static StreamCodec<RegistryFriendlyByteBuf, GunProperties> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,   GunProperties::range,
                ByteBufCodecs.INT,     GunProperties::maxAmmo,
                ByteBufCodecs.INT,     GunProperties::cooldown,
                ByteBufCodecs.INT,     GunProperties::damage,
                Identifier.STREAM_CODEC, GunProperties::decalImage,
                ByteBufCodecs.STRING_UTF8, GunProperties::entityHitKey,
                ByteBufCodecs.STRING_UTF8, GunProperties::blockHitKey,
                ByteBufCodecs.registry(Registries.BLOCK).apply(ByteBufCodecs.list()), GunProperties::breakBlocks,
                GunProperties::new
        );
    }

    public GunItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties);

        this.shootSound = shootSound;

        this.gunProperties = gunProperties;

        this.shakeIntensity = shakeIntensity;
        this.shakeDuration = shakeDuration;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        Integer ammo = itemStack.get(ModComponents.AMMO);

        if (ammo != null) {
            if (owner instanceof Player player && isUsing(owner) && !player.getCooldowns().isOnCooldown(itemStack) && Objects.equals(owner.getWeaponItem(), itemStack) && ammo > 0) {
                ((Player)owner).getCooldowns().addCooldown(itemStack, gunProperties.cooldown);
                itemStack.set(ModComponents.AMMO, ammo - 1);

                shoot(itemStack, level, owner);
            }
        }

        super.inventoryTick(itemStack, level, owner, slot);
    }

    public boolean isUsing(Entity owner) {
        if (!(owner instanceof LivingEntity living)) return false;
        return living.isUsingItem();
    }

    public final void inventoryTickItem(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
    }

    public void shoot(ItemStack itemStack, ServerLevel level, Entity owner) {
        level.playSound(null, owner, shootSound, SoundSource.PLAYERS, 1.0f, 1.0f);

        Vec3 startPos = owner.getEyePosition();
        Vec3 endPos = owner.getEyePosition().add(owner.getLookAngle().scale(gunProperties.range()));

        HitResult hit = Guns.raycast(level, startPos, endPos, owner, gunProperties.breakBlocks());

        applyServerSideHitEffects(level, owner, hit);

        ClientboundShootPayload payload = new ClientboundShootPayload(gunProperties, owner.getUUID(), hit);

        for (ServerPlayer serverPlayer : PlayerLookup.level(level)) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }

        if (owner instanceof Player player) {
            ServerPlayNetworking.send((ServerPlayer) player, new ScreenShakePayload(shakeIntensity, shakeDuration));
        }
    }

    public void applyServerSideHitEffects(ServerLevel level, Entity owner, HitResult hit) {
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.hurt(level.damageSources().mobAttack(asLivingOwner(owner)), gunProperties.damage());

            gunProperties.entityHit().accept(entityHit);
        }
    }

    public static LivingEntity asLivingOwner(Entity owner) {
        return owner instanceof LivingEntity livingOwner ? livingOwner : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        Integer ammo = stack.get(ModComponents.AMMO);
        if (ammo != null) {
            textConsumer.accept(Component.translatable("item.guns.ammo.info", ammo).withStyle(ChatFormatting.GOLD));
        }
    }
}