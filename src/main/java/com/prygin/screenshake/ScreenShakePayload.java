package com.prygin.screenshake;

import com.prygin.Guns;
import com.prygin.item.ClientboundShootPayload;
import com.prygin.item.GunItem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ScreenShakePayload(float intensity, int duration) implements CustomPacketPayload {
    public static final Identifier SCREEN_SHAKE_ID = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "screen_shake");

    public static final CustomPacketPayload.Type<@NotNull ScreenShakePayload> TYPE = new CustomPacketPayload.Type<>(SCREEN_SHAKE_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenShakePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ScreenShakePayload::intensity,
            ByteBufCodecs.INT, ScreenShakePayload::duration,

            ScreenShakePayload::new
    );

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
