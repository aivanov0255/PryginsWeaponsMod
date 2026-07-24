package com.prygin.entity.nijastar;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.prygin.Guns;
import net.minecraft.resources.Identifier;

public class NinjaStarModel extends DefaultedEntityGeoModel<NinjaStarEntity> {
    public NinjaStarModel() {
        super(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "ninja_star"));
    }
}