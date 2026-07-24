package com.prygin.entity.missle;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.prygin.Guns;
import net.minecraft.resources.Identifier;

public class MissleModel extends DefaultedEntityGeoModel<MissleEntity> {
    public MissleModel() {
        super(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "missle"));
    }
}