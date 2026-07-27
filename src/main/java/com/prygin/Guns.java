package com.prygin;

import com.prygin.block.ModBlocks;
import com.prygin.block.block_entity.ModBlockEntities;
import com.prygin.common.ModAttachments;
import com.prygin.creativemodetabs.ModCreativeModeTabs;
import com.prygin.entity.ModEntityTypes;
import com.prygin.item.*;
import com.prygin.item.components.ModComponents;
import com.prygin.item.shulker_blaster.AmmoSyncPayload;
import com.prygin.menu.ModMenuTypes;
import com.prygin.screenshake.ScreenShakePayload;
import com.prygin.sounds.ModSounds;
import com.prygin.trap.SpikeTrap;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Guns implements ModInitializer {
	public static final String MOD_ID = "guns";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.clientboundPlay().register(AmmoSyncPayload.TYPE, AmmoSyncPayload.STREAM_CODEC);

		PayloadTypeRegistry.clientboundPlay().register(
				ClientboundShootPayload.TYPE,
				ClientboundShootPayload.STREAM_CODEC
		);

		PayloadTypeRegistry.clientboundPlay().register(
				ClientboundZoomPayload.TYPE,
				ClientboundZoomPayload.STREAM_CODEC
		);

		PayloadTypeRegistry.clientboundPlay().register(
				ScreenShakePayload.TYPE,
				ScreenShakePayload.STREAM_CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
				GunHitPayload.TYPE,
				GunHitPayload.STREAM_CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
				ServerboundApplyEffectPayload.TYPE,
				ServerboundApplyEffectPayload.STREAM_CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(GunHitPayload.TYPE, (payload, context) -> {

			context.server().execute(() -> {
				ServerPlayer shooter = context.player();

				ServerLevel serverLevel = (ServerLevel) shooter.level();

				Entity target = serverLevel.getEntity(payload.targetID());

				if (target != null) {
					DamageSource source = new DamageSource(
							serverLevel.registryAccess()
									.lookupOrThrow(Registries.DAMAGE_TYPE)
									.get(DamageTypes.GENERIC.identifier())
									.orElseThrow(),
							shooter
					);

					target.hurtServer(serverLevel, source, payload.amount());
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(ServerboundApplyEffectPayload.TYPE, (payload, context) -> {

			context.server().execute(() -> {
				ServerLevel level = context.player().level();

				Entity entity = level.getEntity(payload.entityID());

				if (entity instanceof LivingEntity livingEntity) {
					MobEffectInstance effect = new MobEffectInstance(MobEffects.SLOWNESS, 100, 1, false, false);
					boolean applied = livingEntity.addEffect(effect);
				}
			});
		});

		ModBlocks.initialize();
		ModItems.initialize();
		ModCreativeModeTabs.initialize();
		ModSounds.initialize();
		ModEntityTypes.registerModEntityTypes();
		ModEntityTypes.registerAttributes();

		ModBlockEntities.init();

		ModAttachments.init();

		ModMenuTypes.init();

		ItemComponentTooltipProviderRegistry.addAfter(DataComponents.DAMAGE, ModComponents.SHOTGUN_AMMO_PROPERTIES);

		ServerTickEvents.START_SERVER_TICK.register(server -> {
			for (Player player : server.getPlayerList().getPlayers()) {
				AABB playerBox = player.getBoundingBox();

				boolean isColliding = player.level().getEntities(player, playerBox).stream()
						.anyMatch(entity -> entity instanceof SpikeTrap && playerBox.intersects(entity.getBoundingBox()) && ((SpikeTrap) entity).isActivated()
						);

				AttributeInstance jumpStrengthAttributeInst = getAttributeInstance(player, Attributes.JUMP_STRENGTH);
				AttributeInstance movmentSpeedAttributeInst = getAttributeInstance(player, Attributes.MOVEMENT_SPEED);

				if (isColliding) {
					jumpStrengthAttributeInst.setBaseValue(0.07);
					movmentSpeedAttributeInst.setBaseValue(0.01);
				} else {
					player.getAttributes().resetBaseValue(Attributes.JUMP_STRENGTH);
					player.getAttributes().resetBaseValue(Attributes.MOVEMENT_SPEED);
				}
			}
		});

		LOGGER.info("Hello Fabric world!");
	}

	public static AttributeInstance getAttributeInstance(final Entity target, final Holder<Attribute> attribute) {
        return getLivingEntity(target).getAttributes().getInstance(attribute);
	}

	public static LivingEntity getLivingEntity(final Entity target) {
		if (target instanceof LivingEntity livingEntity) {
			return livingEntity;
		} else {
			return null;
		}
	}

	public static HitResult raycast(Level level, Vec3 startPos, Vec3 endPos, Entity entity, List<Block> breakBlocks) {
		BlockHitResult blockHit = null;
		Vec3 currentStart = startPos;

		while (true) {
			ClipContext currentContext = new ClipContext(
					currentStart,
					endPos,
					ClipContext.Block.OUTLINE,
					ClipContext.Fluid.NONE,
					entity
			);

			BlockHitResult hit = level.clip(currentContext);

			if (hit.getType() == HitResult.Type.MISS) {
				blockHit = hit;
				break;
			}

			BlockPos hitPos = hit.getBlockPos();
			BlockState hitState = level.getBlockState(hitPos);
			Block hitBlock = hitState.getBlock();

			boolean hasCollision = !hitState.getCollisionShape(level, hitPos).isEmpty();
			boolean breakable = breakBlocks.contains(hitBlock);

			if (breakable) {
				breakBlockLikePlayer(level, hitPos, entity);
			}

			if (!hasCollision || breakable) {
				// either it never blocked the ray anyway, or we just broke it — keep going
				currentStart = hit.getLocation().add(entity.getLookAngle().normalize().scale(0.01));
			} else {
				// solid and not breakable — ray stops here
				blockHit = hit;
				break;
			}

			if (currentStart.distanceTo(startPos) >= startPos.distanceTo(endPos)) {
				blockHit = hit;
				break;
			}
		}

		double range = startPos.distanceTo(endPos);
		double blockDist = blockHit.getType() == HitResult.Type.MISS
				? range
				: blockHit.getLocation().distanceTo(startPos);

		AABB searchBox = new AABB(startPos, endPos).inflate(1.0);

		EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
				level,
				entity,
				startPos,
				endPos,
				searchBox,
				e -> !e.isSpectator() && e != entity,
				0.2f
		);

		if (entityHit != null) {
			double entityDist = entityHit.getLocation().distanceTo(startPos);
			if (entityDist < blockDist) {
				return entityHit;
			}
		}

		return blockHit;
	}

	public static void breakBlockLikePlayer(Level level, BlockPos pos, Entity entity) {

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		BlockState state = level.getBlockState(pos);

		if (state.isAir()) {
			return;
		}

		if (entity instanceof ServerPlayer serverPlayer) {
			boolean allowed = net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE
					.invoker()
					.beforeBlockBreak(level, serverPlayer, pos, state, null);

			if (!allowed) return;

			state.getBlock().playerWillDestroy(level, pos, state, serverPlayer);

			boolean removed = level.removeBlock(pos, false);

			if (removed) {
				// Spawn break particles + sound for all nearby clients (same event vanilla uses)
				level.levelEvent(2001, pos, Block.getId(state));

				state.getBlock().playerDestroy(level, serverPlayer, pos, state, level.getBlockEntity(pos), serverPlayer.getMainHandItem());

				net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.AFTER
						.invoker()
						.afterBlockBreak(level, serverPlayer, pos, state, level.getBlockEntity(pos));

			}
		} else {

			boolean removed = serverLevel.destroyBlock(pos, true, entity);

			if (removed) {
				level.levelEvent(2001, pos, Block.getId(state));
			}
		}
	}
}