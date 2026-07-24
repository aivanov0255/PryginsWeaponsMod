package com.prygin.entity.trap;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class TrapRenderer<R extends EntityRenderState & GeoRenderState, T extends AbstractTrap & GeoEntity> extends GeoEntityRenderer<T, R> {
    public TrapRenderer(EntityRendererProvider.Context context, Identifier assetSubpath) {
        super(context, new TrapModel(assetSubpath));
    }
}