package com.prygin.item;

import com.prygin.Guns;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundZoomPayload(float fovModifier) implements CustomPacketPayload {
    public static final Identifier ZOOM_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "zoom");

    public static final CustomPacketPayload.Type<ClientboundZoomPayload> TYPE = new CustomPacketPayload.Type<>(ZOOM_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundZoomPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ClientboundZoomPayload::fovModifier,
            ClientboundZoomPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
