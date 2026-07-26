package com.prygin.item;

import com.prygin.item.components.ModComponents;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class WatergunItem extends GunItem implements PoseHoldable{
    public WatergunItem(Properties properties, GunProperties gunProperties, SoundEvent shootSound, float shakeIntensity, int shakeDuration) {
        super(properties, gunProperties, shootSound, shakeIntensity, shakeDuration);
    }

    @Override
    public boolean posesOppositeArmWhenFree() {
        return true;
    }

    @Override
    public void applyHandPose(ModelPart arm, HumanoidArm armSide, ModelPart otherArm, ItemStack otherStack,
                              InteractionHand hand, ItemStack stack) {

        boolean drivingBothArms = otherStack.isEmpty();

        ModelPart rightArm = armSide == HumanoidArm.RIGHT ? arm : otherArm;
        ModelPart leftArm = armSide == HumanoidArm.RIGHT ? otherArm : arm;

        float forwardPitch = -80.0F * Mth.DEG_TO_RAD;
        float inwardYaw = 12.0F * Mth.DEG_TO_RAD;
        float wristRoll = 6.0F * Mth.DEG_TO_RAD;

        rightArm.xRot = forwardPitch;
        rightArm.yRot = -inwardYaw;
        rightArm.zRot = wristRoll;
        rightArm.x = -3.5F;

        if (drivingBothArms) {
            leftArm.xRot = forwardPitch;
            leftArm.yRot = inwardYaw;
            leftArm.zRot = -wristRoll;
            leftArm.x = 3.5F;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        Integer ammo = stack.get(ModComponents.AMMO);
        if (ammo != null) {
            int num = ammo / 10;
            textConsumer.accept(Component.literal(ammo + "% ")
                    .append(
                            Component.literal("█".repeat(num))
                                    .withColor(Integer.parseInt("0084FF", 16))
                    )
                    .append(
                            Component.literal("█".repeat(10-num))
                                    .withColor(Integer.parseInt("001E3B", 16))
                    )
            );
        }
    }
}
