package com.prygin.item.minigun;

import com.geckolib.renderer.GeoItemRenderer;

public class MinigunRenderer extends GeoItemRenderer<MinigunItem> {
    public MinigunRenderer() {
        super(new MinigunModel());
    }
}
