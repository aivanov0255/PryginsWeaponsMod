package com.prygin.entity.plane;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.prygin.Guns;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;

public class PlaneRenderer<R extends EntityRenderState & GeoRenderState, T extends Plane & GeoEntity> extends GeoEntityRenderer<T, R> {
    public PlaneRenderer(EntityRendererProvider.Context context, Identifier assetSubpath) {
        super(context, new PlaneModel<>(assetSubpath));
    }
}