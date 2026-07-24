package com.prygin.trap;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.prygin.Guns;
import com.prygin.entity.trap.TrapRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.UUID;

public class EffectTrapRenderer<T extends EffectTrap, R extends EntityRenderState & GeoRenderState> extends TrapRenderer<R, T> {
    DataTicket<MobEffectInstance> EFFECT = DataTicket.create("effect", MobEffectInstance.class);
    DataTicket<UUID> OWNER_ID = DataTicket.create("ownerId", UUID.class);
    DataTicket<Boolean> ACTIVATED = DataTicket.create("activated", Boolean.class);

    public EffectTrapRenderer(EntityRendererProvider.Context context) {
        super(context, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "trap/effect_trap"));

        withRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            public void preRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
                R state = renderPassInfo.renderState();
                PoseStack poseStack = renderPassInfo.poseStack();
                int packedOverlay = renderPassInfo.packedOverlay();
                int packedLight = renderPassInfo.packedLight();

                Identifier iconTexture = getEffectIconTexture(state.getGeckolibData(EFFECT));
                if (iconTexture == null) return;
                assert Minecraft.getInstance().player != null;

                if (!Minecraft.getInstance().player.getUUID().equals(state.getGeckolibData(OWNER_ID)) || state.getGeckolibData(ACTIVATED)) return;

                poseStack.pushPose();

                poseStack.translate(0.0, 0.7, 0.0);

                Matrix4f matrix = poseStack.last().pose();

                float tx = matrix.m30();
                float ty = matrix.m31();
                float tz = matrix.m32();

                matrix.identity();

                matrix.translation(tx, ty, tz);

                Quaternionf cameraOrientation = renderPassInfo.cameraState().orientation;
                poseStack.mulPose(cameraOrientation);

                float half = 0.25f;

                renderTasks.submitCustomGeometry(poseStack, RenderTypes.outline(iconTexture), (pose, buffer) -> {
                    buffer.addVertex(pose, -half, -half, 0f).setColor(0xff7373).setUv(0f, 1f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                    buffer.addVertex(pose,  half, -half, 0f).setColor(0xff7373).setUv(1f, 1f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                    buffer.addVertex(pose,  half,  half, 0f).setColor(0xffd373).setUv(1f, 0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                    buffer.addVertex(pose, -half,  half, 0f).setColor(0xffd373).setUv(0f, 0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                });

                renderTasks.submitCustomGeometry(poseStack, RenderTypes.entityCutout(iconTexture), (pose, buffer) -> {
                    buffer.addVertex(pose, -half, -half, 0f).setColor(-1).setUv(0f, 1f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                    buffer.addVertex(pose,  half, -half, 0f).setColor(-1).setUv(1f, 1f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                    buffer.addVertex(pose,  half,  half, 0f).setColor(-1).setUv(1f, 0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                    buffer.addVertex(pose, -half,  half, 0f).setColor(-1).setUv(0f, 0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 0f, 1f);
                });

                poseStack.popPose();
            }
        });
    }

    @ApiStatus.OverrideOnly
    @Override
    public void addRenderData(T animatable, @org.jspecify.annotations.Nullable Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(EFFECT, animatable.getEffect());
        renderState.addGeckolibData(OWNER_ID, animatable.getOwnerId());
        renderState.addGeckolibData(ACTIVATED, animatable.isActivated());
    }

    @Nullable
    private static Identifier getEffectIconTexture(@Nullable MobEffectInstance effectInstance) {
        if (effectInstance == null) return null;
        MobEffect effect = effectInstance.getEffect().value();

        Identifier key = BuiltInRegistries.MOB_EFFECT.getKey(effect);

        if (key == null) return null;

        return Identifier.fromNamespaceAndPath(
                key.getNamespace(),
                "textures/mob_effect/" + key.getPath() + ".png"
        );
    }
}