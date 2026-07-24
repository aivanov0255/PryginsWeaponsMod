package com.prygin.entity.trap;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.prygin.Guns;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractTrap extends Entity implements GeoEntity {
    private static final EntityDataAccessor<String> DATA_OWNER_ID = SynchedEntityData.defineId(AbstractTrap.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_ACTIVATED = SynchedEntityData.defineId(AbstractTrap.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AbstractTrap(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNER_ID, uuid.toString());
    }

    @Nullable
    public UUID getOwnerId() {
        return UUID.fromString(this.entityData.get(DATA_OWNER_ID));
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (level() instanceof ServerLevel serverLevel && !isActivated()) {
            for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
                if (distanceTo(player) < 1 && !player.getUUID().equals(getOwnerId())) {
                    activate(player, serverLevel);
                    break;
                }

            }
        }

        setRot(0, 0);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (player.isCrouching() && player.getUUID().equals(getOwnerId())) {
            discard();
            player.addItem(new ItemStack(getTrapItem()));
            player.swing(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public abstract Item getTrapItem();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>( "activate_controller", state -> PlayState.STOP)
                    .triggerableAnim("activate", getActivateAnimation()));
    }

    public boolean isActivated() {
        return this.entityData.get(DATA_ACTIVATED);
    }

    private void setActivated(boolean activated) {
        this.entityData.set(DATA_ACTIVATED, activated);
    }

    private void activate(ServerPlayer player, ServerLevel serverLevel) {
        onActivate(player, serverLevel);
        triggerAnim("activate_controller","activate");
        setActivated(true);
    }

    public abstract RawAnimation getActivateAnimation();
    public abstract void onActivate(ServerPlayer player, ServerLevel level);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_OWNER_ID, UUID.randomUUID().toString());
        entityData.define(DATA_ACTIVATED, false);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setOwnerId(UUID.fromString(input.getString("Owner").get()));
        setActivated(input.getBooleanOr("Activated", false));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putString("Owner", this.getOwnerId().toString());
        output.putBoolean("Activated", isActivated());
    }
}
