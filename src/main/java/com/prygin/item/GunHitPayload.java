package com.prygin.item;

import com.prygin.Guns;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record GunHitPayload(UUID targetID, int amount) implements CustomPacketPayload {

    public static final Identifier HIT_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "hit");

    public static final CustomPacketPayload.Type<GunHitPayload> TYPE = new CustomPacketPayload.Type<>(HIT_PAYLOAD_ID);

    public static final StreamCodec<ByteBuf, GunHitPayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, GunHitPayload::targetID,
            ByteBufCodecs.INT, GunHitPayload::amount,
            GunHitPayload::new
        );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
