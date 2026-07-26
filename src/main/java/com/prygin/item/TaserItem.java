package com.prygin.item;

import com.prygin.Guns;
import com.prygin.item.components.ModComponents;
import com.prygin.screenshake.ScreenShakePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TaserItem extends Item implements Chargable, PoseHoldable {
    public TaserItem(Properties properties) {
        super(properties.component(ModComponents.AMMO, 100));
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        Integer charge = itemStack.get(ModComponents.AMMO);

        if (isUsing(owner) && charge > 0) {
            whileUsing(itemStack, level, owner);
        }
    }

    public boolean isUsing(Entity owner) {
        if (!(owner instanceof LivingEntity living)) return false;
        return living.isUsingItem();
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.PASS;
    }

    public void whileUsing(ItemStack itemStack, ServerLevel level, Entity owner) {
        itemStack.set(ModComponents.AMMO, itemStack.get(ModComponents.AMMO) - 1);

        Vec3 startPos = owner.getEyePosition();
        Vec3 endPos = owner.getEyePosition().add(owner.getLookAngle().scale(5));

        HitResult hitResult = Guns.raycast(level, startPos, endPos, owner, new ArrayList<>());

        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof ServerPlayer player) {
                ServerPlayNetworking.send(player, new ScreenShakePayload(5, 20));
            }
            if (entityHitResult.getEntity() instanceof LivingEntity) entityHitResult.getEntity().hurtServer(level, owner.damageSources().mobAttack((LivingEntity) entityHitResult.getEntity()), 0.1f);
        }
    }

    @Override
    public int chargingSpeed() {
        return 50;
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
        int num = stack.get(ModComponents.AMMO) / 10;
        textConsumer.accept(Component.literal((int)stack.get(ModComponents.AMMO) + "% ")
                .append(
                        Component.literal("█".repeat(num))
                                .withColor(Integer.parseInt("66ff00", 16))
                )
                .append(
                        Component.literal("█".repeat(10-num))
                                .withColor(Integer.parseInt("0b1c00", 16))
                )
        );
    }
}
