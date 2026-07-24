package com.prygin.item;

import com.prygin.Guns;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.UUID;

public record ServerboundApplyEffectPayload(UUID entityID, MobEffectInstance effect) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundApplyEffectPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Guns.MOD_ID, "apply_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundApplyEffectPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ServerboundApplyEffectPayload::entityID,
            MobEffectInstance.STREAM_CODEC, ServerboundApplyEffectPayload::effect,
            ServerboundApplyEffectPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
