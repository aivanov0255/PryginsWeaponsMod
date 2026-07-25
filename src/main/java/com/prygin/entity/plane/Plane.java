package com.prygin.entity.plane;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class Plane extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> DATA_SPINNING = SynchedEntityData.defineId(Plane.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LANDING_GEAR_OUT = SynchedEntityData.defineId(Plane.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Quaternionfc> DATA_ORIENTATION = SynchedEntityData.defineId(Plane.class, EntityDataSerializers.QUATERNION);
    private static final EntityDataAccessor<Float> DATA_THROTTLE = SynchedEntityData.defineId(Plane.class, EntityDataSerializers.FLOAT);

    private static final float THROTTLE_STEP = 0.02f;

    private static final RawAnimation SPINNING_LANDED = RawAnimation.begin().then("spinning_land", LoopType.LOOP);
    private static final RawAnimation SPINNING_FLYING = RawAnimation.begin().then("spinning_fly", LoopType.LOOP);

    public Plane(EntityType<?> type, Level level) {
        super(type, level);
    }

    public boolean isSpinning() {
        return this.entityData.get(DATA_SPINNING);
    }

    public void setSpinning(boolean spinning) {
        this.entityData.set(DATA_SPINNING, spinning);
    }

    public boolean isLandingGearOut() {
        return this.entityData.get(DATA_LANDING_GEAR_OUT);
    }

    public void setLandingGearOut(boolean landingGearOut) {
        this.entityData.set(DATA_LANDING_GEAR_OUT, landingGearOut);
    }

    public Quaternionfc getOrientation() {
        return this.entityData.get(DATA_ORIENTATION);
    }

    public void setOrientation(Quaternionf orientation) {
        this.entityData.set(DATA_ORIENTATION, orientation);
    }

    public float getThrottle() {
        return this.entityData.get(DATA_THROTTLE);
    }

    public void setThrottle(float throttle) {
        this.entityData.set(DATA_THROTTLE, Mth.clamp(throttle, 0.0f, 1.0f));
    }

    public void increaseThrottle() {
        setThrottle(getThrottle() + THROTTLE_STEP);
    }

    public void decreaseThrottle() {
        setThrottle(getThrottle() - THROTTLE_STEP);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("spinning_controller", 5, state -> {
            if (isSpinning()) {
                if (isLandingGearOut()) {
                    state.setAnimation(SPINNING_LANDED);
                } else {
                    state.setAnimation(SPINNING_FLYING);
                }
                return PlayState.CONTINUE;
            }
            return PlayState.PAUSE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_SPINNING, false);
        entityData.define(DATA_LANDING_GEAR_OUT, true);
        entityData.define(DATA_ORIENTATION, new Quaternionf());
        entityData.define(DATA_THROTTLE, 0.0f);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setSpinning(input.getBooleanOr("Spinning", false));
        setLandingGearOut(input.getBooleanOr("LandingGearOut", true));
        setThrottle(input.getFloatOr("Throttle", 0.0f));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("Spinning", isSpinning());
        output.putBoolean("LandingGearOut", isLandingGearOut());
        output.putFloat("Throttle", getThrottle());
    }
}