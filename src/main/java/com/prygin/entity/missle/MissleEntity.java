package com.prygin.entity.missle;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.prygin.entity.ModEntityTypes;
import com.prygin.item.ModItems;
import com.prygin.sounds.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class MissleEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation SPIN_ANIM = RawAnimation.begin().then("main", LoopType.LOOP);

    public MissleEntity(Level level, Player player) {
        super(ModEntityTypes.MISSLE, player, level, new ItemStack(ModItems.MISSLE), null);
    }

    public MissleEntity(EntityType<MissleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>("spinning", 0, state -> {
                    if (!isInGround()) return state.setAndContinue(SPIN_ANIM);

                    state.controller().reset();
                    return PlayState.STOP;
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.MISSLE);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(0.25f, 0.25f);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        level().explode(this, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 5, true, Level.ExplosionInteraction.BLOCK);
        discard();
    }
}
