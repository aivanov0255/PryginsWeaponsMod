package com.prygin.mixin;

import com.prygin.access.HumanoidRenderStateExtension;
import com.prygin.item.PoseHoldable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<S extends HumanoidRenderState> {

    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void poseHoldableHands(S state, CallbackInfo ci) {
        if (!(state instanceof HumanoidRenderStateExtension extension)) {
            return;
        }

        HumanoidArm mainArmSide = state.mainArm;

        ItemStack mainHandStack = extension.guns$getMainHandStack();
        ItemStack offHandStack = extension.guns$getOffHandStack();

        ModelPart mainArmModel = mainArmSide == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
        ModelPart offArmModel = mainArmSide == HumanoidArm.RIGHT ? this.leftArm : this.rightArm;

        HumanoidArm offArmSide = mainArmSide == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;

        boolean mainClaimedOffArm = applyPoseHoldable(
                mainHandStack, mainArmModel, mainArmSide, offArmModel, offHandStack,
                InteractionHand.MAIN_HAND);

        if (!mainClaimedOffArm) {
            applyPoseHoldable(offHandStack, offArmModel, offArmSide, mainArmModel, mainHandStack,
                    InteractionHand.OFF_HAND);
        }
    }

    private boolean applyPoseHoldable(ItemStack stack, ModelPart arm, HumanoidArm armSide, ModelPart otherArm,
                                      ItemStack otherStack, InteractionHand hand) {
        if (!(stack.getItem() instanceof PoseHoldable poseHoldable)) {
            return false;
        }

        poseHoldable.applyHandPose(arm, armSide, otherArm, otherStack, hand, stack);

        boolean otherIsFree = otherStack.isEmpty() || !(otherStack.getItem() instanceof PoseHoldable);
        return poseHoldable.posesOppositeArmWhenFree() && otherIsFree;
    }
}