package com.prygin.entity.shockwave;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ShockwaveEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation MAIN_ANIM = RawAnimation.begin().then("main", LoopType.HOLD_ON_LAST_FRAME);

    public static final EntityDataAccessor<Float> DATA_SCALE_ID = SynchedEntityData.defineId(ShockwaveEntity.class, EntityDataSerializers.FLOAT);

    public static final int LIFESPAN_TICKS = 21;

    public ShockwaveEntity(EntityType<? extends ShockwaveEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > LIFESPAN_TICKS) {
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        float scale = input.getFloatOr("EntityScale", 1.0f);

        this.entityData.set(DATA_SCALE_ID, scale);
        this.refreshDimensions();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("EntityScale", this.entityData.get(DATA_SCALE_ID));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SCALE_ID, 1.0f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("shockwave", state -> PlayState.STOP)
                .triggerableAnim("main", MAIN_ANIM));
    }

    public void triggerShockwave() {
        triggerAnim("shockwave", "main");
    }

    public void setScale(float scale) {
        this.entityData.set(DATA_SCALE_ID, scale);
    }

    public float getScale() {
        return this.entityData.get(DATA_SCALE_ID);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float currentScale = this.entityData.get(DATA_SCALE_ID);
        return super.getDimensions(pose).scale(currentScale);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_SCALE_ID.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}