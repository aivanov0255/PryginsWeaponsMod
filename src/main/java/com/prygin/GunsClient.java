package com.prygin;

import com.geckolib.renderer.specialty.DirectionalProjectileRenderer;
import com.prygin.block.block_entity.ModBlockEntities;
import com.prygin.block.block_entity.RechargerBlockEntityRenderer;
import com.prygin.block.block_entity.SkyBlockEntityRenderer;
import com.prygin.entity.ModEntityTypes;
import com.prygin.entity.battle_axe.BattleAxeEntity;
import com.prygin.entity.battle_axe.BattleAxeModel;
import com.prygin.entity.boomerang.BoomerangRenderer;
import com.prygin.entity.missle.MissleEntity;
import com.prygin.entity.missle.MissleModel;
import com.prygin.entity.nijastar.NinjaStarEntity;
import com.prygin.entity.nijastar.NinjaStarModel;
import com.prygin.entity.plane.PlaneRenderer;
import com.prygin.entity.scythe_zombie.ScytheZombieRenderer;
import com.prygin.entity.shockwave.ShockwaveRenderer;
import com.prygin.entity.trap.TrapRenderer;
import com.prygin.item.*;
import com.prygin.item.components.ShotgunChamberTooltip;
import com.prygin.item.components.ShotgunChamberTooltipComponent;
import com.prygin.item.selectprops.ModConditionalItemModelProperties;
import com.prygin.item.selectprops.ModItemTintSources;
import com.prygin.item.selectprops.ModRangeSelectItemModelProperties;
import com.prygin.item.shulker_blaster.AmmoSyncPayload;
import com.prygin.item.shulker_blaster.ShulkerBlaster;
import com.prygin.menu.ModMenuTypes;
import com.prygin.rendering.HitDecalRenderer;
import com.prygin.rendering.ScopeOverlay;
import com.prygin.screens.AmmoBenchScreen;
import com.prygin.screens.RechargerScreen;
import com.prygin.screenshake.ScreenShakeManager;
import com.prygin.screenshake.ScreenShakePayload;
import com.prygin.trap.EffectTrapRenderer;
import com.prygin.trap.MobTrapRenderer;
import com.prygin.zoom.ZoomManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class GunsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundShootPayload.TYPE, (payload, context) -> {
            GunItem.GunProperties props = payload.props();
            HitResult hit = payload.hit();

            if (hit instanceof EntityHitResult entityHit) {
            } else if (hit instanceof BlockHitResult blockHit) {
                HitDecalRenderer.getInstance().spawnDecalFromRaycast(blockHit, props.decalImage());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(AmmoSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> ShulkerBlaster.AMMO_CACHE.put(payload.instanceId(), payload.ammo()));
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundZoomPayload.TYPE, (payload, context) -> {
            ZoomManager.setZoom(payload.fovModifier());
        });

        ClientPlayNetworking.registerGlobalReceiver(ScreenShakePayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ScreenShakeManager.shake(payload.intensity(), payload.duration());
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ScreenShakeManager.tick();
        });

        EntityRendererRegistry.register(ModEntityTypes.SCYTHE_ZOMBIE, ScytheZombieRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.SHOCKWAVE, ShockwaveRenderer::new);

        EntityRendererRegistry.register(ModEntityTypes.NINJA_STAR,
                (context) -> new DirectionalProjectileRenderer<NinjaStarEntity, ArrowRenderState>(context, new NinjaStarModel()));

        EntityRendererRegistry.register(ModEntityTypes.MISSLE,
                (context) -> new DirectionalProjectileRenderer<MissleEntity, ArrowRenderState>(context, new MissleModel()));

        EntityRendererRegistry.register(ModEntityTypes.BATTLE_AXE,
                (context) -> new DirectionalProjectileRenderer<BattleAxeEntity, ArrowRenderState>(context, new BattleAxeModel()));

        EntityRendererRegistry.register(ModEntityTypes.SPIKE_TRAP,
                context -> new TrapRenderer<>(context, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "trap/spike_trap")));

        EntityRendererRegistry.register(ModEntityTypes.SLOWNESS_TRAP, EffectTrapRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.POISON_TRAP, EffectTrapRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.BLINDNESS_TRAP, EffectTrapRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.GLOWING_TRAP, EffectTrapRenderer::new);

        EntityRendererRegistry.register(ModEntityTypes.CREEPER_TRAP, MobTrapRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.SILVERFISH_TRAP, MobTrapRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.TNT_TRAP, MobTrapRenderer::new);

        EntityRendererRegistry.register(ModEntityTypes.GRANADE, ThrownItemRenderer::new);

        BlockEntityRenderers.register(ModBlockEntities.SKY_DAY_BLOCK_ENTITY, (context) -> new SkyBlockEntityRenderer());
        BlockEntityRenderers.register(ModBlockEntities.SKY_NIGHT_BLOCK_ENTITY, (context) -> new SkyBlockEntityRenderer());
        BlockEntityRenderers.register(ModBlockEntities.SKY_SUNSET_BLOCK_ENTITY, (context) -> new SkyBlockEntityRenderer());
        BlockEntityRenderers.register(ModBlockEntities.RECHARGER, RechargerBlockEntityRenderer::new);

        EntityRendererRegistry.register(ModEntityTypes.PLANE, (context -> new PlaneRenderer<>(context, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "simple_plane"))));
        EntityRendererRegistry.register(ModEntityTypes.BOOMERANG, (context -> new BoomerangRenderer<>(context, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "boomerang"))));

        MenuScreens.register(ModMenuTypes.RECHARGER, RechargerScreen::new);
        MenuScreens.register(ModMenuTypes.SHOTGUN_CHAMBER, ShotgunChamberScreen::new);
        MenuScreens.register(ModMenuTypes.AMMO_BENCH_MENU, AmmoBenchScreen::new);

        ScopeOverlay.register();

        ClientTooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof ShotgunChamberTooltip chamber) {
                return new ShotgunChamberTooltipComponent(chamber);
            }
            return null;
        });

        ModConditionalItemModelProperties.bootstrap();
        ModItemTintSources.bootstrap();
        ModRangeSelectItemModelProperties.bootstrap();
    }
}