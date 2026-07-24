package com.prygin.block.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.prygin.item.Chargable;
import com.prygin.item.CyberCannonItem;
import com.prygin.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RechargerBlockEntityRenderer implements BlockEntityRenderer<RechargerBlockEntity, RechargerBlockEntityRenderState> {

    private static final float SLAB_TOP_Y = 9.0f/16.0f;
    private static final float HOVER_OFFSET = 0.3f;

    ItemModelResolver itemModelResolver;

    float tick;

    public RechargerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RechargerBlockEntityRenderState createRenderState() {
        return new RechargerBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(RechargerBlockEntity blockEntity, RechargerBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.setHasChargable(blockEntity.getItem(0).getItem() instanceof Chargable);

        tick = blockEntity.life + partialTicks;

        Level level = blockEntity.getLevel();
        itemModelResolver.updateForTopItem(state.item, blockEntity.getItem(0), ItemDisplayContext.GROUND, level instanceof ClientLevel clientLevel ? clientLevel : null, null, 1);
    }

    @Override
    public void submit(RechargerBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.hasChargable()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, SLAB_TOP_Y + HOVER_OFFSET, 0.5);

        poseStack.mulPose(Axis.YP.rotationDegrees(tick));

        poseStack.scale(0.8f, 0.8f, 0.8f);

        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0x00000000);

        poseStack.popPose();
    }
}