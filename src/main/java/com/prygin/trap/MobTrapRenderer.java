package com.prygin.trap;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prygin.Guns;
import com.prygin.entity.trap.TrapRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.UUID;

public class MobTrapRenderer<T extends MobTrap, R extends EntityRenderState & GeoRenderState> extends TrapRenderer<R, T> {
    DataTicket<EntityType> ENTIY_TYPE = DataTicket.create("entity_type", EntityType.class);
    DataTicket<UUID> OWNER_ID = DataTicket.create("ownerId", UUID.class);
    DataTicket<Boolean> ACTIVATED = DataTicket.create("activated", Boolean.class);

    float partialTick;
    float time;

    boolean frozen;

    public MobTrapRenderer(EntityRendererProvider.Context context) {
        super(context, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "trap/mob_trap"));

        withRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            public void preRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
                R state = renderPassInfo.renderState();
                PoseStack poseStack = renderPassInfo.poseStack();

                EntityType type = renderPassInfo.getGeckolibData(ENTIY_TYPE);

                assert Minecraft.getInstance().player != null;
                if (!Minecraft.getInstance().player.getUUID().equals(state.getGeckolibData(OWNER_ID))) return;

                poseStack.pushPose();

                poseStack.translate(0.0, 0.3, 0.0);

                Entity temporaryEntity = type.create(Minecraft.getInstance().level, EntitySpawnReason.TRIGGERED);
                EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
                EntityRenderer<Entity, EntityRenderState> baseRenderer = (EntityRenderer<Entity, EntityRenderState>) dispatcher.getRenderer(temporaryEntity);

                var renderState = baseRenderer.createRenderState();
                try {
                    if (frozen) {
                        baseRenderer.extractRenderState(temporaryEntity, renderState, 0.0f);
                    } else {
                        baseRenderer.extractRenderState(temporaryEntity, renderState, time);
                    }
                } catch (Exception e) {}

                poseStack.mulPose(Axis.YP.rotationDegrees(time*3));

                poseStack.scale(0.3f, 0.3f, 0.3f);
                baseRenderer.submit(renderState, poseStack, renderTasks, renderPassInfo.cameraState());

                poseStack.popPose();
            }
        });
    }

    @ApiStatus.OverrideOnly
    @Override
    public void addRenderData(T animatable, @org.jspecify.annotations.Nullable Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(ENTIY_TYPE, animatable.getMobType());
        renderState.addGeckolibData(OWNER_ID, animatable.getOwnerId());
        renderState.addGeckolibData(ACTIVATED, animatable.isActivated());

        this.partialTick = partialTick;

        this.time = animatable.level().getGameTime() + partialTick;

        this.frozen = animatable.isFrozen();
    }
}