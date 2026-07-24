package com.prygin.item;

import com.prygin.Guns;
import com.prygin.entity.ModEntityTypes;
import com.prygin.entity.shockwave.ShockwaveEntity;
import com.prygin.item.components.ModComponents;
import com.prygin.screenshake.ScreenShakePayload;
import com.prygin.sounds.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CyberCannonItem extends GunItem implements Chargable {
    private static final float RADIUS = 8;

    boolean shouldShoot = false;

    public CyberCannonItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        Integer charge = itemStack.get(ModComponents.AMMO);

        if (owner instanceof Player player && this.isUsing(owner) && !player.getCooldowns().isOnCooldown(itemStack) && Objects.equals(owner.getWeaponItem(), itemStack)) {
            level.playSound(null, owner, shootSound, SoundSource.PLAYERS, 1.0f, 1.0f);

            shouldShoot = true;

            CompletableFuture.delayedExecutor(12400, TimeUnit.MILLISECONDS).execute(() -> {
                level.getServer().execute(() -> {
                    if (owner.isAlive() && Objects.equals(player.getMainHandItem(), itemStack) && shouldShoot && charge >= 10) {
                        shoot(itemStack, level, owner);
                        shouldShoot = false;
                    }
                });
            });

            player.getCooldowns().addCooldown(itemStack, gunProperties.cooldown());
        }

        if (owner instanceof Player player && !player.getMainHandItem().equals(itemStack) && shouldShoot) {
            shouldShoot = false;

            ClientboundStopSoundPacket stopSoundPacket = new ClientboundStopSoundPacket(ModSounds.CYBER_CANNON_SHOOT.location(), SoundSource.PLAYERS);

            for(ServerPlayer p : level.players()) {
                p.connection.send(stopSoundPacket);
            }
        }

        inventoryTickItem(itemStack, level, owner, slot);
    }

    @Override
    public void shoot(ItemStack itemStack, ServerLevel level, Entity owner) {
        itemStack.set(ModComponents.AMMO, itemStack.get(ModComponents.AMMO) - 10);

        Vec3 startPos = owner.getEyePosition();
        Vec3 endPos = startPos.add(owner.getLookAngle().scale(gunProperties.range()));

        HitResult hit = Guns.raycast(level, startPos, endPos, owner, gunProperties.breakBlocks());
        Vec3 hitLoc = hit.getLocation();

        level.explode(owner, null, null, hitLoc, 0, true, Level.ExplosionInteraction.BLOCK);

        Random random = new Random();

        BlockPos centerPos = BlockPos.containing(hitLoc);
        for (int i = 0; i < 50; i++) {
            clearSphereAsync(level, centerPos.offset(
                            -(int)RADIUS/2 + random.nextInt((int)RADIUS),
                            -(int)RADIUS/2 + random.nextInt((int)(RADIUS*1.1/2)),
                            -(int)RADIUS/2 + random.nextInt((int)RADIUS)),
                    (int) (RADIUS * random.nextDouble()/1.2), false);
        }

        ShockwaveEntity shockwave = ModEntityTypes.SHOCKWAVE.create(level, EntitySpawnReason.TRIGGERED);
        if (shockwave != null) {
            shockwave.setPos(hitLoc.x - 0.5, hitLoc.y, hitLoc.z + 0.5);
            shockwave.setScale(16 * RADIUS);
            level.addFreshEntity(shockwave);
            shockwave.triggerShockwave();
        }

        super.applyServerSideHitEffects(level, owner, hit);

        ClientboundShootPayload payload = new ClientboundShootPayload(gunProperties, owner.getUUID(), hit);
        for (ServerPlayer serverPlayer : PlayerLookup.level(level)) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }

        if (owner instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new ScreenShakePayload(shakeIntensity, shakeDuration));
        }
    }

    public void clearSphereAsync(ServerLevel world, BlockPos center, int radius, boolean hollow) {
        CompletableFuture
                .supplyAsync(() -> computeSpherePositions(center, radius, hollow))
                .thenAcceptAsync(positions -> applyClear(world, positions), world.getServer());
    }

    private List<BlockPos> computeSpherePositions(BlockPos center, int radius, boolean hollow) {
        int sqRadius = radius * radius;
        int innerSqRadius = (radius - 1) * (radius - 1);
        List<BlockPos> positions = new ArrayList<>();

        // Optimization: Loop variables bounded directly to minimize unnecessary iterations
        for (int x = -radius; x <= radius; x++) {
            int xSq = x * x;
            for (int y = -radius; y <= radius; y++) {
                int xySq = xSq + y * y;
                if (xySq > sqRadius && !hollow) continue; // Early pruning

                for (int z = -radius; z <= radius; z++) {
                    int distanceSq = xySq + z * z;

                    if (distanceSq <= sqRadius && (!hollow || distanceSq > innerSqRadius)) {
                        // Immutable allocation inside loop is fine, but cached blockpos can be used if profiling drops
                        positions.add(center.offset(x, y, z));
                    }
                }
            }
        }
        return positions;
    }

    private void applyClear(ServerLevel world, List<BlockPos> positions) {
        BlockState airState = Blocks.AIR.defaultBlockState();

        List<Block> fireBlocks = new ArrayList<>();

        fireBlocks.add(Blocks.MAGMA_BLOCK);
        fireBlocks.add(Blocks.SMOOTH_BASALT);
        fireBlocks.add(Blocks.BASALT);
        fireBlocks.add(Blocks.FIRE);
        fireBlocks.add(Blocks.NETHERRACK);
        fireBlocks.add(Blocks.NETHER_BRICKS);

        Random random = new Random();
        for (BlockPos pos : positions) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock().getExplosionResistance() > 0) {
                float val = random.nextFloat(50);
                if (val < 50 - state.getBlock().getExplosionResistance()) {
                    if (val < 15) {
                        boolean shouldPlace = false;

                        for (BlockPos pos1 : getBlockNeighbors(pos)) {
                            if (!positions.contains(pos1)) {
                                shouldPlace = true;
                                break;
                            }
                        }

                        if (shouldPlace) {
                            int choice = random.nextInt(fireBlocks.size());
                            world.setBlock(pos, fireBlocks.get(choice).defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    } else {
                        world.setBlock(pos, airState, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

    private List<BlockPos> getBlockNeighbors(BlockPos pos) {
        List<BlockPos> positions = new ArrayList<>();

        positions.add(pos.offset(-1, 0, 0));
        positions.add(pos.offset(1, 0, 0));
        positions.add(pos.offset(0, -1, 0));
        positions.add(pos.offset(0, 1, 0));
        positions.add(pos.offset(0, 0, -1));
        positions.add(pos.offset(0, 0, 1));

        return positions;
    }

    @Override
    public int chargingSpeed() {
        return 100;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        int num = stack.get(ModComponents.AMMO) / 10;
        textConsumer.accept(Component.literal((int)stack.get(ModComponents.AMMO) + "% ")
                .append(
                        Component.literal("█".repeat(num))
                                .withColor(Integer.parseInt("ff1cd9", 16))
                )
                .append(
                        Component.literal("█".repeat(10-num))
                                .withColor(Integer.parseInt("2e0e29", 16))
                )
        );
    }
}