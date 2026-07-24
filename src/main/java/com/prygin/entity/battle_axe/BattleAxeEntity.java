package com.prygin.entity.battle_axe;

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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class BattleAxeEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation SPIN_ANIM = RawAnimation.begin().then("main", LoopType.LOOP);
    public static final RawAnimation STOP_ANIM = RawAnimation.begin().then("hit", LoopType.LOOP);

    public BattleAxeEntity(Level level, Player player) {
        super(ModEntityTypes.BATTLE_AXE, player, level, new ItemStack(ModItems.BATTLE_AXE), null);

        setBaseDamage(10);
    }

    public BattleAxeEntity(EntityType<BattleAxeEntity> type, Level level) {
        super(type, level);

        setBaseDamage(10);
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ANVIL_LAND;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        float dmg = 8.0F;
        Entity currentOwner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, (Entity)(currentOwner == null ? this : currentOwner));
        Level var7;

        if (!entity.getUUID().equals(this.owner.getUUID())) {
            if (entity.hurtOrSimulate(damageSource, dmg)) {
                if (entity.is(EntityTypes.ENDERMAN)) {
                    return;
                }

                var7 = this.level();
                if (var7 instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)var7;
                    EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(serverLevel, entity, damageSource, this.getWeaponItem(), (weapon) -> this.kill(serverLevel));
                }

                if (entity instanceof LivingEntity) {
                    LivingEntity mob = (LivingEntity)entity;
                    this.doKnockback(mob, damageSource);
                    this.doPostHurtEffects(mob);
                }
            }

            this.deflect(ProjectileDeflection.REVERSE, entity, this.owner, false);
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.5, 2, 1.5));
            this.playSound(SoundEvents.ANVIL_LAND, 1F, 1F);
        } else {
            if (owner.getEntity(level(), Entity.class) instanceof Player player) {
                player.addItem(this.getDefaultPickupItem());
                discard();
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>("spinning", 0, state -> {
                    if (!isInGround()) return state.setAndContinue(SPIN_ANIM);
                    else {
                        state.setAnimation(STOP_ANIM);
                        return PlayState.PAUSE;
                    }
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.BATTLE_AXE);
    }
}
