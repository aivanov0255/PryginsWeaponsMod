package com.prygin.entity.boomerang;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BoomerangEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<String> DATA_OWNER_ID = SynchedEntityData.defineId(AbstractTrap.class, EntityDataSerializers.STRING);

    public static final RawAnimation SPIN_ANIM = RawAnimation.begin().then("spin", LoopType.LOOP);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BoomerangEntity(EntityType<?> type, Level level) {
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>("spinning", 0, state -> state.setAndContinue(SPIN_ANIM))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_OWNER_ID, UUID.randomUUID().toString());
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setOwnerId(UUID.fromString(input.getString("Owner").get()));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putString("Owner", this.getOwnerId().toString());
    }
}
