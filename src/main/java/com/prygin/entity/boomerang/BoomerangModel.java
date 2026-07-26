package com.prygin.entity.boomerang;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.prygin.entity.plane.Plane;
import net.minecraft.resources.Identifier;

public class BoomerangModel<T extends BoomerangEntity & GeoAnimatable> extends DefaultedEntityGeoModel<T> {

    public BoomerangModel(Identifier assetSubpath) {
        super(assetSubpath);
    }
}
