package com.prygin.block.block_entity;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prygin.Guns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SkyBlockEntityRenderer implements BlockEntityRenderer<SkyBlockEntity, SkyBlockEntityRenderState> {

    // ----- cube pipeline -----
    private static final RenderPipeline SKY_BOX_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "pipeline/sky_block_dome"))
                    .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
                    .withCull(true)
                    .build()
    );
    private static final RenderType SKY_BOX_RENDER_TYPE = RenderType.create(
            "sky_block_dome", RenderSetup.builder(SKY_BOX_PIPELINE).createRenderSetup()
    );

    // ----- color helpers -----
    private static final int COLOR_DAY_TOP       = 0xFF4A80FF;
    private static final int COLOR_DAY_HORIZON   = 0xFFB4D2FF;
    private static final int COLOR_NIGHT_TOP     = 0xFF03030C;
    private static final int COLOR_NIGHT_HORIZON = 0xFF0A0A1A;
    private static final int COLOR_GLOW          = 0xFFFF8040;

    private static int lerpRGB(int a, int b, float t) {
        t = Mth.clamp(t, 0f, 1f);
        int aa = (a >> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        return (Math.round(aa + (ba - aa) * t) << 24)
                | (Math.round(ar + (br - ar) * t) << 16)
                | (Math.round(ag + (bg - ag) * t) << 8)
                |  Math.round(ab + (bb - ab) * t);
    }

    private static int withAlpha(int rgb, float alpha) {
        int a = Math.round(Mth.clamp(alpha, 0f, 1f) * 255f);
        return (a << 24) | (rgb & 0x00FFFFFF);
    }

    private static float dayNightFactor(float timeOfDay) {
        double angle = (timeOfDay - 0.25) * (Math.PI * 2);
        return (float) (Math.cos(angle) * 0.5 + 0.5);
    }

    private static float horizonGlowFactor(float timeOfDay) {
        float distToDawn = Math.min(Math.abs(timeOfDay), Math.abs(timeOfDay - 1f));
        float distToDusk = Math.abs(timeOfDay - 0.5f);
        float minDist = Math.min(distToDawn, distToDusk);
        return Mth.clamp(1f - minDist / 0.08f, 0f, 1f);
    }

    public static int skyColorTop(float timeOfDay) {
        int base = lerpRGB(COLOR_NIGHT_TOP, COLOR_DAY_TOP, dayNightFactor(timeOfDay));
        return lerpRGB(base, COLOR_GLOW, horizonGlowFactor(timeOfDay) * 0.3f);
    }

    public static int skyColorHorizon(float timeOfDay) {
        int base = lerpRGB(COLOR_NIGHT_HORIZON, COLOR_DAY_HORIZON, dayNightFactor(timeOfDay));
        return lerpRGB(base, COLOR_GLOW, horizonGlowFactor(timeOfDay));
    }

    private static int skyColorForDirection(Vec3 dir, float timeOfDay) {
        float verticalT = (float) Mth.clamp(dir.y * 0.5 + 0.5, 0.0, 1.0);
        int base = lerpRGB(skyColorHorizon(timeOfDay), skyColorTop(timeOfDay), verticalT);
        float levelness = 1f - Math.abs((float) dir.y);
        float glow = horizonGlowFactor(timeOfDay) * Mth.clamp(levelness, 0f, 1f);
        return lerpRGB(base, COLOR_GLOW, glow * 0.5f);
    }

    private static float timeOfDay(long dayTime) {
        return (float) (Math.floorMod(dayTime, 24000L) / 24000.0);
    }

    @Override
    public SkyBlockEntityRenderState createRenderState() {
        return new SkyBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(SkyBlockEntity skyBlock, SkyBlockEntityRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(skyBlock, state, partialTicks, cameraPosition, breakProgress);

        float timeOfDay = timeOfDay(skyBlock.time);

        state.setTime(skyBlock.time);
        state.setVisibleFaces(computeVisibleFaces(skyBlock));
        state.setCornerColors(computeCornerColors(skyBlock, cameraPosition, timeOfDay));

        float dayNight = dayNightFactor(timeOfDay);
        float sunAlpha = Mth.clamp(dayNight * 1.5f, 0f, 1f);
        float moonAlpha = Mth.clamp((1f - dayNight) * 1.5f, 0f, 1f);
        float starBrightness = Mth.clamp((0.5f - dayNight) * 2f, 0f, 1f);
        int moonPhaseIndex = Math.floorMod((int) (skyBlock.time / 24000L), 8);

        int skyTop = skyColorTop(timeOfDay);
        int skyHorizon = skyColorHorizon(timeOfDay);
    }

    private static int computeVisibleFaces(SkyBlockEntity skyBlock) {
        Level level = skyBlock.getLevel();
        if (level == null) {
            return 0b111111;
        }
        BlockPos pos = skyBlock.getBlockPos();
        Block sameBlock = skyBlock.getBlockState().getBlock();

        int mask = 0;
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            boolean touchesSkyBlock = level.getBlockState(neighborPos).is(sameBlock);
            if (!touchesSkyBlock) {
                mask |= (1 << dir.get3DDataValue());
            }
        }
        return mask;
    }

    private static int[] computeCornerColors(SkyBlockEntity skyBlock, Vec3 cameraPosition, float timeOfDay) {
        BlockPos pos = skyBlock.getBlockPos();
        int[] colors = new int[8];
        for (int dz = 0; dz <= 1; dz++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dx = 0; dx <= 1; dx++) {
                    double wx = pos.getX() + dx;
                    double wy = pos.getY() + dy;
                    double wz = pos.getZ() + dz;
                    Vec3 dir = new Vec3(wx - cameraPosition.x, wy - cameraPosition.y, wz - cameraPosition.z).normalize();
                    int idx = dx | (dy << 1) | (dz << 2);
                    colors[idx] = skyColorForDirection(dir, timeOfDay);
                }
            }
        }
        return colors;
    }

    @Override
    public void submit(SkyBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        submitCube(state, poseStack, submitNodeCollector);
    }

    private void submitCube(SkyBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        submitNodeCollector.submitCustomGeometry(poseStack, SKY_BOX_RENDER_TYPE, (pose, vc) -> {
            float min = 0.0f;
            float max = 1.0f;

            int visibleFaces = state.getVisibleFaces();
            int[] c = state.getCornerColors();
            int c000 = c[0], c100 = c[1], c010 = c[2], c110 = c[3];
            int c001 = c[4], c101 = c[5], c011 = c[6], c111 = c[7];

            if ((visibleFaces & (1 << 0)) != 0) {
                vc.addVertex(pose, max, min, min).setColor(c100);
                vc.addVertex(pose, max, min, max).setColor(c101);
                vc.addVertex(pose, min, min, max).setColor(c001);
                vc.addVertex(pose, min, min, min).setColor(c000);
            }
            if ((visibleFaces & (1 << 1)) != 0) {
                vc.addVertex(pose, min, max, max).setColor(c011);
                vc.addVertex(pose, max, max, max).setColor(c111);
                vc.addVertex(pose, max, max, min).setColor(c110);
                vc.addVertex(pose, min, max, min).setColor(c010);
            }
            if ((visibleFaces & (1 << 2)) != 0) {
                vc.addVertex(pose, min, max, min).setColor(c010);
                vc.addVertex(pose, max, max, min).setColor(c110);
                vc.addVertex(pose, max, min, min).setColor(c100);
                vc.addVertex(pose, min, min, min).setColor(c000);
            }
            if ((visibleFaces & (1 << 3)) != 0) {
                vc.addVertex(pose, max, min, max).setColor(c101);
                vc.addVertex(pose, max, max, max).setColor(c111);
                vc.addVertex(pose, min, max, max).setColor(c011);
                vc.addVertex(pose, min, min, max).setColor(c001);
            }
            if ((visibleFaces & (1 << 4)) != 0) {
                vc.addVertex(pose, min, min, max).setColor(c001);
                vc.addVertex(pose, min, max, max).setColor(c011);
                vc.addVertex(pose, min, max, min).setColor(c010);
                vc.addVertex(pose, min, min, min).setColor(c000);
            }
            if ((visibleFaces & (1 << 5)) != 0) {
                vc.addVertex(pose, max, max, min).setColor(c110);
                vc.addVertex(pose, max, max, max).setColor(c111);
                vc.addVertex(pose, max, min, max).setColor(c101);
                vc.addVertex(pose, max, min, min).setColor(c100);
            }
        });
    }
}