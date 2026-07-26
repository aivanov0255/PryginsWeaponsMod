package com.prygin.entity.boomerang;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public class BoomerangRenderer<R extends EntityRenderState & GeoRenderState, T extends BoomerangEntity> extends GeoEntityRenderer<T, R> {
    public static final DataTicket<Float> STARTING_ROTATION_TICKET = DataTicket.create("starting_rot", Float.class);
    public static final DataTicket<Vec3> OWNER_POSITION_TICKET = DataTicket.create("owner_pos", Vec3.class);
    public static final DataTicket<BoomerangEntity> ENTITY_DATA_TICKET = DataTicket.create("entity", BoomerangEntity.class);
    public static final DataTicket<Float> TICKS_TICKET = DataTicket.create("ticks", Float.class);

    public BoomerangRenderer(EntityRendererProvider.Context context, Identifier assetSubpath) {
        super(context, new BoomerangModel<>(assetSubpath));
    }

    @ApiStatus.OverrideOnly
    @Override
    public void addRenderData(T animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(STARTING_ROTATION_TICKET, animatable.getStartingRot());
        renderState.addGeckolibData(OWNER_POSITION_TICKET, animatable.level().getEntity(animatable.getOwnerId()).position());
        renderState.addGeckolibData(ENTITY_DATA_TICKET, animatable);
        renderState.addGeckolibData(TICKS_TICKET, animatable.tickCount + partialTick);
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        PoseStack poseStack = renderPassInfo.poseStack();
        poseStack.pushPose();

        float ticks = renderPassInfo.renderState().getOrDefaultGeckolibData(TICKS_TICKET, 0f);
        float startingRot = renderPassInfo.renderState().getOrDefaultGeckolibData(STARTING_ROTATION_TICKET, 0f);

        double angle = ticks * (2 * Math.PI / 40.0);
        double x = 5 * Math.cos(-angle + startingRot + Math.PI/2) + 5 * Math.sin(startingRot);
        double z = 5 * Math.sin(-angle + startingRot + Math.PI/2) - 5 * Math.cos(startingRot);

        poseStack.translate(x, 0, z);

        super.preRenderPass(renderPassInfo, renderTasks);
        poseStack.popPose();
    }
}