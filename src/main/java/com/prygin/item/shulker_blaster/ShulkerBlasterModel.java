package com.prygin.item.shulker_blaster;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.prygin.Guns;
import com.prygin.item.minigun.MinigunItem;
import net.minecraft.resources.Identifier;

public class ShulkerBlasterModel extends GeoModel<ShulkerBlaster> {
    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/item/shulker_blaster.png");
    }

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shulker_blaster");
    }

    @Override
    public Identifier getAnimationResource(ShulkerBlaster minigunItem) {
        return Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/shulker_blaster");
    }
}