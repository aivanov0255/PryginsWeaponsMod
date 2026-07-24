package com.prygin.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record ShotgunAmmoProperties(int explodeRange, int hitDamage, boolean teleportTargetRandomly, Optional<List<MobEffectInstance>> statusEffect) implements TooltipProvider {
    public static final Codec<ShotgunAmmoProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("range").forGetter(ShotgunAmmoProperties::explodeRange),
            Codec.INT.fieldOf("hit_damage").forGetter(ShotgunAmmoProperties::hitDamage),
            Codec.BOOL.fieldOf("teleport_target_randomly").forGetter(ShotgunAmmoProperties::teleportTargetRandomly),
            MobEffectInstance.CODEC.listOf().optionalFieldOf("status_effect").forGetter(ShotgunAmmoProperties::statusEffect)
    ).apply(instance, ShotgunAmmoProperties::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShotgunAmmoProperties> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ShotgunAmmoProperties::explodeRange,
            ByteBufCodecs.VAR_INT, ShotgunAmmoProperties::hitDamage,
            ByteBufCodecs.BOOL, ShotgunAmmoProperties::teleportTargetRandomly,
            ByteBufCodecs.optional(MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list())), ShotgunAmmoProperties::statusEffect,
            ShotgunAmmoProperties::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("tooltip.guns.hit_damage", hitDamage).withColor(TextColor.LIGHT_PURPLE));
        if (explodeRange > 0) consumer.accept(Component.translatable("tooltip.guns.explode_range", explodeRange).withColor(TextColor.LIGHT_PURPLE));
        if (teleportTargetRandomly) consumer.accept(Component.translatable("tooltip.guns.teleport_target_randomly").withColor(TextColor.LIGHT_PURPLE));

        if (statusEffect.isEmpty()) return;
        consumer.accept(Component.translatable("tooltip.guns.status_effect")
                .withColor(TextColor.LIGHT_PURPLE));

        for (MobEffectInstance effect : statusEffect.get()) {
            consumer.accept(Component.translatable(effect.getDescriptionId()).withColor(TextColor.LIGHT_PURPLE));
        }
    }
}