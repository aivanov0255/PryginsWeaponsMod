package com.prygin.item;

import com.prygin.Guns;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public record ClientboundShootPayload(GunItem.GunProperties props, UUID id, HitResult hit) implements CustomPacketPayload {
    public static final Identifier SHOOT_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "shoot");

    public static final CustomPacketPayload.Type<ClientboundShootPayload> TYPE = new CustomPacketPayload.Type<>(SHOOT_PAYLOAD_ID);

    private static final StreamCodec<RegistryFriendlyByteBuf, HitResult> HIT_RESULT_STREAM_CODEC = StreamCodec.of(
            (buf, hit) -> {
                if (hit instanceof BlockHitResult blockHit) {
                    buf.writeByte(0);
                    writeVec3(buf, hit.getLocation());
                    buf.writeBlockPos(blockHit.getBlockPos());
                    buf.writeEnum(blockHit.getDirection());
                } else if (hit instanceof EntityHitResult entityHit) {
                    buf.writeByte(1);
                    writeVec3(buf, hit.getLocation());
                    buf.writeUUID(entityHit.getEntity().getUUID()); // works on FriendlyByteBuf-family
                } else {
                    buf.writeByte(2);
                    writeVec3(buf, hit.getLocation());
                }
            },
            buf -> {
                int tag = buf.readByte();
                Vec3 loc = readVec3(buf);

                return switch (tag) {
                    case 0 -> {
                        BlockPos pos = buf.readBlockPos();
                        Direction dir = buf.readEnum(Direction.class);
                        yield new BlockHitResult(loc, dir, pos, false);
                    }
                    case 1 -> {
                        UUID entityId = buf.readUUID();
                        Entity entity = resolveEntityClientSide(entityId);
                        yield new EntityHitResult(entity, loc);
                    }
                    default -> BlockHitResult.miss(loc, Direction.UP, BlockPos.ZERO);
                };
            }
    );

    private static void writeVec3(ByteBuf buf, Vec3 vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }

    private static Vec3 readVec3(ByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    private static Entity resolveEntityClientSide(UUID entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;

        for (Entity entity : level.entitiesForRendering()) {
            if (entity.getUUID().equals(entityId)) {
                return entity;
            }
        }

        return null;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundShootPayload> STREAM_CODEC = StreamCodec.composite(
            GunItem.GunProperties.STREAM_CODEC, ClientboundShootPayload::props,
            UUIDUtil.STREAM_CODEC, ClientboundShootPayload::id,
            HIT_RESULT_STREAM_CODEC, ClientboundShootPayload::hit,
            ClientboundShootPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
