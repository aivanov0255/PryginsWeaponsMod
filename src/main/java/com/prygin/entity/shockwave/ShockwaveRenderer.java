package com.prygin.entity.shockwave;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ShockwaveRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<ShockwaveEntity, R> {
    public static final DataTicket<Float> SCALE_TICKET = DataTicket.create("shockwave_scale", Float.class);

    public ShockwaveRenderer(EntityRendererProvider.Context context) {
        super(context, new ShockwaveModel());
    }

    @Override
    public void addRenderData(ShockwaveEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(SCALE_TICKET, animatable.getScale());
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.renderState().getOrDefaultGeckolibData(SCALE_TICKET, 1f);

        super.scaleModelForRender(renderPassInfo, widthScale * scale, heightScale);
    }

    @Override
    public int getRenderColor(ShockwaveEntity animatable, @Nullable Void relatedObject, float partialTick) {
        int color = super.getRenderColor(animatable, relatedObject, partialTick);

        float age = animatable.tickCount + partialTick;
        float fade = 1f - Mth.clamp(age / ShockwaveEntity.LIFESPAN_TICKS, 0f, 1f);
        int alpha = Mth.ceil(255 * fade);

        return ARGB.color(alpha, color);
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}