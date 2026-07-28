package com.prygin.rope;

import com.prygin.Guns;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundRopePayload(Rope rope, CreateType createType) implements CustomPacketPayload {
    public enum CreateType {
        CREATE(0),
        REMOVE(1);

        public static final StreamCodec<? super RegistryFriendlyByteBuf, CreateType> STREAM_CODEC =
                ByteBufCodecs.idMapper(CreateType::byId, CreateType::getId).cast();

        final int id;

        CreateType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static CreateType byId(int id) {
            return switch (id) {
                case 0 -> CREATE;
                case 1 -> REMOVE;
                default -> null;
            };
        }
    }

    public static final Identifier ROPE_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "rope");

    public static final CustomPacketPayload.Type<ClientboundRopePayload> TYPE = new CustomPacketPayload.Type<>(ROPE_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRopePayload> STREAM_CODEC = StreamCodec.composite(
            Rope.STREAM_CODEC, ClientboundRopePayload::rope,
            CreateType.STREAM_CODEC, ClientboundRopePayload::createType,
            ClientboundRopePayload::new
    );

    @Override
    public Type<ClientboundRopePayload> type() {
        return TYPE;
    }
}
