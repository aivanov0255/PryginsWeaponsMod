package com.prygin.item.shulker_blaster;

import com.geckolib.renderer.GeoItemRenderer;
import com.prygin.item.minigun.MinigunItem;
import com.prygin.item.minigun.MinigunModel;

public class ShulkerBlasterRenderer extends GeoItemRenderer<ShulkerBlaster> {
    public ShulkerBlasterRenderer() {
        super(new ShulkerBlasterModel());
    }
}
