package com.prygin.rope;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class RopeRenderer {

    private RopeRenderer() {
    }

    public static void register() {
        LevelRenderEvents.COLLECT_SUBMITS.register(RopeRenderer::renderAll);
    }

    private static void renderAll(LevelRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        Level level = client.level;
        if (level == null || RopeManager.getRopes().isEmpty()) {
            return;
        }

        PoseStack poseStack = context.poseStack();

        Vec3 cameraPos = client.gameRenderer.mainCamera().position();
        float partialTick = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        if (poseStack == null) {
            return;
        }

        for (Rope rope : RopeManager.getRopes()) {
            Entity entityA = findEntity(level, rope.getEntityAId());
            Entity entityB = findEntity(level, rope.getEntityBId());

            if (entityA == null || entityB == null || !entityA.isAlive() || !entityB.isAlive()) {
                continue;
            }

            RopeTextureUtil.ensureRepeating(rope.getTexture());
            renderRope(context, poseStack, level, rope, entityA, entityB, cameraPos, partialTick);
        }
    }

    private static Entity findEntity(Level level, UUID id) {
        return level.getEntity(id);
    }

    private static void renderRope(LevelRenderContext context, PoseStack poseStack, Level level,
                                   Rope rope, Entity entityA, Entity entityB, Vec3 cameraPos, float partialTick) {

        Vec3 worldPosA = entityA.getPosition(partialTick).add(0, entityA.getBbHeight() * 0.5, 0);
        Vec3 worldPosB = entityB.getPosition(partialTick).add(0, entityB.getBbHeight() * 0.5, 0);
        Vec3 posA = worldPosA.subtract(cameraPos);
        Vec3 posB = worldPosB.subtract(cameraPos);

        RenderType renderType = RopeRenderTypes.ropeTranslucent(rope.getTexture());

        int segments = rope.getSegments();
        float halfWidth = rope.getWidth() * 0.5f;
        float textureLength = rope.getTextureLength();

        Vec3[] points = new Vec3[segments + 1];
        float[] cumulativeDistance = new float[segments + 1];
        int[] light = new int[segments + 1];

        Vec3 prev = null;
        float distanceSoFar = 0f;
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            Vec3 point = catenaryPoint(posA, posB, rope.getSag(), t);
            points[i] = point;

            if (prev != null) {
                distanceSoFar += (float) point.distanceTo(prev);
            }
            cumulativeDistance[i] = distanceSoFar;
            prev = point;

            BlockPos blockPos = BlockPos.containing(point.add(cameraPos));

            int lightA = getPackedLight(level, entityA.blockPosition());
            int lightB = getPackedLight(level, entityB.blockPosition());
            int sampledLight = getPackedLight(level, blockPos);

            light[i] = sampledLight > 0
                    ? sampledLight
                    : (int) Mth.lerp(t, lightA, lightB);
        }

        context.submitNodeCollector().submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            for (int i = 0; i < segments; i++) {
                Vec3 p0 = points[i];
                Vec3 p1 = points[i + 1];

                float v0 = cumulativeDistance[i] / textureLength;
                float v1 = cumulativeDistance[i + 1] / textureLength;

                int light0 = light[i];
                int light1 = light[i + 1];

                // Compute horizontal perpendicular vector relative to segment direction
                Vec3 dir = p1.subtract(p0);
                Vec3 normal = new Vec3(-dir.z, 0, dir.x);
                if (normal.lengthSqr() < 1e-5) {
                    normal = new Vec3(1, 0, 0); // Fallback for perfectly vertical segments
                } else {
                    normal = normal.normalize();
                }

                // 1. Vertical Quad
                vertex(buffer, pose, p0.add(0, halfWidth, 0), 0f, v0, light0);
                vertex(buffer, pose, p0.subtract(0, halfWidth, 0), 1f, v0, light0);
                vertex(buffer, pose, p1.subtract(0, halfWidth, 0), 1f, v1, light1);
                vertex(buffer, pose, p1.add(0, halfWidth, 0), 0f, v1, light1);

                // 2. Horizontal Quad (Cross plane)
                Vec3 sideOffset = normal.scale(halfWidth);
                vertex(buffer, pose, p0.add(sideOffset), 0f, v0, light0);
                vertex(buffer, pose, p0.subtract(sideOffset), 1f, v0, light0);
                vertex(buffer, pose, p1.subtract(sideOffset), 1f, v1, light1);
                vertex(buffer, pose, p1.add(sideOffset), 0f, v1, light1);
            }
        });
    }

    private static int getPackedLight(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightCoordsUtil.pack(blockLight, skyLight);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose,
                               Vec3 pos, float u, float v, int light) {
        buffer.addVertex(pose, (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0f, 1f, 0f);
    }

    private static Vec3 catenaryPoint(Vec3 a, Vec3 b, float sag, float t) {
        Vec3 lerp = a.lerp(b, t);
        double droop = Math.sin(Math.PI * t) * sag;
        return lerp.subtract(0, droop, 0);
    }
}