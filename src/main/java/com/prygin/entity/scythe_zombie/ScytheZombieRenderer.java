package com.prygin.entity.scythe_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.prygin.item.ZombieScythe;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.zombie.Zombie;

import java.awt.*;

public class ScytheZombieRenderer extends ZombieRenderer {

    Color timerColor = new Color(94, 151, 255);
    Color timerColorOther = new Color(25, 107, 255);

    public static class ScytheZombieRenderState extends ZombieRenderState {
        public float scytheCooldownRatio = 0.0f;
    }

    public ScytheZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ZombieRenderState createRenderState() {
        return new ScytheZombieRenderState();
    }

    @Override
    public void extractRenderState(Zombie entity, ZombieRenderState genericState, float partialTick) {
        super.extractRenderState(entity, genericState, partialTick);
        if (genericState instanceof ScytheZombieRenderState state) {
            if (entity instanceof ScytheZombie scytheZombie) {
                float remainingTime = scytheZombie.getScytheCooldown();
                state.scytheCooldownRatio = Mth.clamp(remainingTime / ZombieScythe.COOLDOWN_TICKS, 0.0f, 1.0f);
            } else {
                state.scytheCooldownRatio = 0.0f;
            }
        }
    }

    @Override
    protected void submitNameDisplay(ZombieRenderState genericState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submitNameDisplay(genericState, poseStack, submitNodeCollector, camera);
        if (!(genericState instanceof ScytheZombieRenderState state)) return;

        poseStack.pushPose();
        poseStack.translate(0.0f, state.eyeHeight + 1f, 0.0f);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(0.025f, 0.025f, 0.025f);

        float totalWidth = 30.0f;
        float height = 4.0f;
        float halfWidth = totalWidth / 2.0f;
        float filledWidth = totalWidth * state.scytheCooldownRatio;

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugTriangleFan(), (pose, buffer) -> {
            var matrix = pose.pose();

            float bgMinX = -halfWidth - 1.0f;
            float bgMaxX = halfWidth + 1.0f;
            float bgMinY = -1.0f;
            float bgMaxY = height + 1.0f;

            buffer.addVertex(matrix, bgMinX, bgMinY, 0.0f).setColor(0.2f, 0.2f, 0.2f, 1.0f);
            buffer.addVertex(matrix, bgMaxX, bgMinY, 0.0f).setColor(0.2f, 0.2f, 0.2f, 1.0f);
            buffer.addVertex(matrix, bgMaxX, bgMaxY, 0.0f).setColor(0.2f, 0.2f, 0.2f, 1.0f);
            buffer.addVertex(matrix, bgMinX, bgMaxY, 0.0f).setColor(0.2f, 0.2f, 0.2f, 1.0f);
        });

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugTriangleFan(), (pose, buffer) -> {
            var matrix = pose.pose();

            float filledXEnd = -halfWidth + filledWidth;

            buffer.addVertex(matrix, -halfWidth, 0.0f, -0.05f).setColor(timerColorOther.getRed()/255.0f, timerColorOther.getGreen()/255.0f, timerColorOther.getBlue()/255.0f, 1.0f);
            buffer.addVertex(matrix, filledXEnd, 0.0f, -0.05f).setColor(timerColorOther.getRed()/255.0f, timerColorOther.getGreen()/255.0f, timerColorOther.getBlue()/255.0f, 1.0f);
            buffer.addVertex(matrix, filledXEnd, height, -0.05f).setColor(timerColor.getRed()/255.0f, timerColor.getGreen()/255.0f, timerColor.getBlue()/255.0f, 1.0f);
            buffer.addVertex(matrix, -halfWidth, height, -0.05f).setColor(timerColor.getRed()/255.0f, timerColor.getGreen()/255.0f, timerColor.getBlue()/255.0f, 1.0f);
        });

        poseStack.popPose();
    }
}
