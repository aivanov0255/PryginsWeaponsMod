package com.prygin.entity.trap;

import com.geckolib.model.DefaultedEntityGeoModel;
import net.minecraft.resources.Identifier;

public class TrapModel<T extends AbstractTrap> extends DefaultedEntityGeoModel<T> {

    public TrapModel(Identifier assetSubpath) {
        super(assetSubpath);
    }
}
