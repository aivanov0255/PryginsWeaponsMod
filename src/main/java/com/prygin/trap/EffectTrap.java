package com.prygin.trap;

import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.prygin.entity.trap.AbstractTrap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class EffectTrap extends AbstractTrap {
    private MobEffectInstance effectInstance;

    public EffectTrap(EntityType<?> type, Level level, final Holder<MobEffect> effect, final int duration, final int amplifier, final boolean ambient, final boolean visible, final boolean showIcon) {
        super(type, level);

        effectInstance = new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
    }

    public MobEffectInstance getEffect() {
        return effectInstance;
    }

    @Override
    public abstract Item getTrapItem();

    @Override
    public RawAnimation getActivateAnimation() {
        return RawAnimation.begin().then("activate", LoopType.HOLD_ON_LAST_FRAME);
    }

    @Override
    public void onActivate(ServerPlayer player, ServerLevel level) {
        ItemStack itemStack = new ItemStack(Items.SPLASH_POTION);

        Optional<Holder<Potion>> potionOptional = Optional.empty();
        Optional<Integer> colorOptional = Optional.empty();
        Optional<String> nameOptional = Optional.empty();

        List<MobEffectInstance> effects = new ArrayList<>();

        effects.add(getEffect());

        itemStack.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        potionOptional,
                        colorOptional,
                        effects,
                        nameOptional
                )
        );

        ThrownSplashPotion potion = new ThrownSplashPotion(level, getX(), getY(), getZ(), itemStack);

        potion.shoot(0, 1, 0, 0.5f, 0);

        level.addFreshEntity(potion);
    }
}
