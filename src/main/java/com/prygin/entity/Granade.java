package com.prygin.entity;

import com.prygin.item.ModItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class Granade extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> DATA_FUSE =
            SynchedEntityData.defineId(Granade.class, EntityDataSerializers.INT);

    public Granade(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public Granade(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level level, ItemStack itemStack) {
        super(type, x, y, z, level, itemStack);
    }

    public Granade(EntityType<? extends ThrowableItemProjectile> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    public Granade(final Level level, final LivingEntity mob, final ItemStack itemStack) {
        super(ModEntityTypes.GRANADE, mob, level, itemStack);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.GRANADE;
    }

    @Override
    protected void onHit(final HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHit) {
            setDeltaMovement(-this.getDeltaMovement().x/4, -this.getDeltaMovement().y/4, -this.getDeltaMovement().z/4);
        } else if (hitResult instanceof BlockHitResult blockHit) {
            BlockState state = level().getBlockState(blockHit.getBlockPos());

            float frictionMult = Math.clamp(state.getBlock().getFriction() + 0.1f, 0, 1);
            float bounceMult = state.getBlock().getBounceRestitution() + 0.1f;

            switch (blockHit.getDirection()) {
                case UP, DOWN:
                    setDeltaMovement(this.getDeltaMovement().x*frictionMult,
                            -this.getDeltaMovement().y*bounceMult,
                            this.getDeltaMovement().z*frictionMult);
                    break;
                case NORTH, SOUTH:
                    setDeltaMovement(this.getDeltaMovement().x*frictionMult,
                            this.getDeltaMovement().y*frictionMult,
                            -this.getDeltaMovement().z*bounceMult);
                    break;
                case EAST, WEST:
                    setDeltaMovement(-this.getDeltaMovement().x*bounceMult,
                            this.getDeltaMovement().y*frictionMult,
                            this.getDeltaMovement().z*frictionMult);
                    break;
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUSE, 60);
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE, fuse);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            return;
        }

        if (tickCount >= getFuse()) {
            level().explode(this, getX(), getY(), getZ(), 2, Level.ExplosionInteraction.BLOCK);
            discard();
        }
    }

    @Override
    public boolean shouldRender(double camX, double camY, double camZ) {
        return true;
    }
}
