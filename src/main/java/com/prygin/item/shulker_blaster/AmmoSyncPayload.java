package com.prygin.item.shulker_blaster;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AmmoSyncPayload(long instanceId, int ammo) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AmmoSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("guns", "ammo_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AmmoSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, AmmoSyncPayload::instanceId,
            ByteBufCodecs.VAR_INT, AmmoSyncPayload::ammo,
            AmmoSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}