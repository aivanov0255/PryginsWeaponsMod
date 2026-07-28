package com.prygin.item;

import com.prygin.Guns;
import com.prygin.entity.ModEntityTypes;
import com.prygin.entity.hook.HookEntity;
import com.prygin.item.components.ModComponents;
import com.prygin.rope.RopeManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookItem extends Item implements PoseHoldable {
    public GrapplingHookItem(Properties properties) {
        super(properties.component(ModComponents.HAS_HOOK, true));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean hasHook = Boolean.TRUE.equals(stack.get(ModComponents.HAS_HOOK));

        if (hasHook) {
            if (!level.isClientSide()) {
                stack.set(ModComponents.HAS_HOOK, false);

                HookEntity hook = new HookEntity(ModEntityTypes.HOOK, level);

                float pitch = (float) Math.toRadians(player.getXRot());
                float yaw = (float) Math.toRadians(player.getYRot());

                double x = -Math.sin(yaw) * Math.cos(pitch);
                double y = -Math.sin(pitch);
                double z = Math.cos(yaw) * Math.cos(pitch);

                double speed = 2;
                Vec3 velocity = new Vec3(x * speed, y * speed, z * speed);

                hook.snapTo(player.getEyePosition());
                hook.setDeltaMovement(velocity);

                hook.setOwner(player);

                level.addFreshEntity(hook);

                RopeManager.createRope(
                        hook,
                        player,
                        Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/rope/grappling_hook_rope.png"),
                        0.5f, 0.5f, 1, 16
                );

                hook.setXRot(-player.getXRot());
                hook.setYRot(-player.getYRot());
            }
            return InteractionResult.SUCCESS;
        } else {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
    }

    public HookEntity getHook(Player player) {
        if (player.level().isClientSide()) return null;
        for (Entity entity : ((ServerLevel) player.level()).getAllEntities()) {
            if (entity instanceof HookEntity hook) {
                if (hook.getOwner() != null && hook.getOwner().is(player)) {
                    return hook;
                }
            }
        }
        return null;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!(entity instanceof ServerPlayer player)) return;

        HookEntity hook = getHook(player);

        if (hook != null && !hook.isRemoved() && hook.inGround()) {
            Vec3 hookPos = hook.position();
            Vec3 playerPos = player.position();
            Vec3 pullVector = hookPos.subtract(playerPos);

            if (pullVector.length() > 1.5) {
                Vec3 pullDirection = pullVector.normalize();
                double pullStrength = 0.3;

                Vec3 newMovement = player.getDeltaMovement().add(pullDirection.scale(pullStrength));
                player.setDeltaMovement(newMovement);

                player.connection.send(new ClientboundSetEntityMotionPacket(player));
                player.resetFallDistance();
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!level.isClientSide() && entity instanceof Player player) {
            HookEntity hook = getHook(player);

            if (hook != null) {
                hook.discard();
            }

            stack.set(ModComponents.HAS_HOOK, true);

            return true;
        }

        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
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
}