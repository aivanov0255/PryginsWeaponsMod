package com.prygin.entity.hook;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.mojang.datafixers.types.templates.Hook;
import com.prygin.Guns;
import com.prygin.entity.missle.MissleEntity;
import net.minecraft.resources.Identifier;

public class HookModel extends DefaultedEntityGeoModel<HookEntity> {
    public HookModel() {
        super(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "hook"));
    }
}