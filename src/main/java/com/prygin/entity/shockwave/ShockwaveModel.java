package com.prygin.entity.shockwave;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.prygin.Guns;
import net.minecraft.resources.Identifier;

public class ShockwaveModel extends DefaultedEntityGeoModel<ShockwaveEntity> {
    public ShockwaveModel() {
        super(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "shockwave"));
    }
}
