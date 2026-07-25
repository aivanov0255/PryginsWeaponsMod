package com.prygin.entity.plane;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.resources.Identifier;

public class PlaneModel<T extends Plane> extends DefaultedEntityGeoModel<T> {

    public PlaneModel(Identifier assetSubpath) {
        super(assetSubpath);
    }
}
