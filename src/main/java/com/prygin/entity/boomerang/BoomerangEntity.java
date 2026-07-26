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
import com.prygin.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BoomerangEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<String> DATA_OWNER_ID = SynchedEntityData.defineId(BoomerangEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_STARTING_ROT = SynchedEntityData.defineId(BoomerangEntity.class, EntityDataSerializers.FLOAT);

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
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            Vec3 owner = new Vec3(0, 0, 0);
            try {
                owner = level().getEntity(getOwnerId()).position();
            } catch (Exception e) {
                discard();
            }
            double angle = tickCount * (2 * Math.PI / 40.0);
            double x = owner.x() + 5 * Math.cos(angle + Math.PI / 2) - 5 * Math.sin(getStartingRot());
            double y = owner.y();
            double z = owner.z() + 5 * Math.sin(angle + Math.PI / 2) - 5 * Math.cos(getStartingRot());
            this.setPos(x, y, z);

            if (tickCount >= 40) {
                discard();
                if (level().getEntity(getOwnerId()) instanceof Player player) {
                    player.getInventory().add(new ItemStack(ModItems.BOOMERANG));
                }
            }
        }
    }

    public float getStartingRot() {
        return this.entityData.get(DATA_STARTING_ROT);
    }

    public void setStartingRot(float startingRot) {
        this.entityData.set(DATA_STARTING_ROT, startingRot);
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
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_OWNER_ID, UUID.randomUUID().toString());
        entityData.define(DATA_STARTING_ROT, 0f);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setOwnerId(UUID.fromString(input.getString("Owner").get()));
        setStartingRot(input.getFloatOr("StartingRot", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putString("Owner", this.getOwnerId().toString());
        output.putFloat("StartingRot", getStartingRot());
    }
}
