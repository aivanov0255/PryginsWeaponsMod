package com.prygin.entity.boomerang;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.prygin.entity.plane.Plane;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public class BoomerangRenderer<R extends EntityRenderState & GeoRenderState, T extends BoomerangEntity & GeoEntity> extends GeoEntityRenderer<T, R> {
    public BoomerangRenderer(EntityRendererProvider.Context context, Identifier assetSubpath) {
        super(context, new BoomerangModel<>(assetSubpath));
    }
}