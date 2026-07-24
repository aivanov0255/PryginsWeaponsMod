package com.prygin.item.minigun;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.prygin.Guns;
import net.minecraft.resources.Identifier;

public class MinigunModel extends GeoModel<MinigunItem> {
    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/item/mini_gun.png");
    }

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/mini_gun");
    }

    @Override
    public Identifier getAnimationResource(MinigunItem minigunItem) {
        return Identifier.fromNamespaceAndPath(Guns.MOD_ID, "item/mini_gun");
    }
}