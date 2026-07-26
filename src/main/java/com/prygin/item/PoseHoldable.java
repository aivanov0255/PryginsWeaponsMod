package com.prygin.item;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface PoseHoldable {

    void applyHandPose(ModelPart arm, HumanoidArm armSide, ModelPart otherArm, ItemStack otherStack,
                       InteractionHand hand, ItemStack stack);

    default boolean posesOppositeArmWhenFree() {
        return false;
    }
}