package com.prygin.entity.battle_axe;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.prygin.Guns;
import net.minecraft.resources.Identifier;

public class BattleAxeModel extends DefaultedEntityGeoModel<BattleAxeEntity> {
    public BattleAxeModel() {
        super(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "battle_axe"));
    }
}