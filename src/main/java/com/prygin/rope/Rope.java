package com.prygin.rope;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class Rope {

    public static final StreamCodec<? super RegistryFriendlyByteBuf, Rope> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Rope::getEntityAId,
            UUIDUtil.STREAM_CODEC, Rope::getEntityBId,
            Identifier.STREAM_CODEC, Rope::getTexture,
            ByteBufCodecs.FLOAT, Rope::getWidth,
            ByteBufCodecs.FLOAT, Rope::getSag,
            ByteBufCodecs.FLOAT, Rope::getTextureLength,
            ByteBufCodecs.INT, Rope::getSegments,
            Rope::new
    );

    private UUID entityAId;
    private UUID entityBId;

    private Identifier texture;
    private float width;
    private float sag;
    private float textureLength;
    private int segments;

    public Rope(Rope rope) {
        this.entityAId = rope.entityAId;
        this.entityBId = rope.entityBId;
        this.texture = rope.texture;
        this.width = rope.width;
        this.sag = rope.sag;
        this.textureLength = rope.textureLength;
        this.segments = Math.max(2, rope.segments);
    }

    public Rope(UUID entityAId, UUID entityBId, Identifier texture) {
        this(entityAId, entityBId, texture, 0.15f, 0.5f, 1.0f, 16);
    }

    public Rope(UUID entityAId, UUID entityBId, Identifier texture,
                float width, float sag, float textureLength, int segments) {
        this.entityAId = entityAId;
        this.entityBId = entityBId;
        this.texture = texture;
        this.width = width;
        this.sag = sag;
        this.textureLength = textureLength;
        this.segments = Math.max(2, segments);
    }

    public Rope(Entity entityA, Entity entityB, Identifier texture) {
        this(entityA, entityB, texture, 0.15f, 0.5f, 1.0f, 16);
    }

    public Rope(Entity entityA, Entity entityB, Identifier texture,
                float width, float sag, float textureLength, int segments) {
        this.entityAId = entityA.getUUID();
        this.entityBId = entityB.getUUID();
        this.texture = texture;
        this.width = width;
        this.sag = sag;
        this.textureLength = textureLength;
        this.segments = Math.max(2, segments);
    }

    public UUID getEntityAId() {
        return entityAId;
    }

    public UUID getEntityBId() {
        return entityBId;
    }

    public void setEntityAId(UUID id) {
        entityAId = id;
    }

    public void setEntityBId(UUID id) {
        entityBId = id;
    }

    public Identifier getTexture() {
        return texture;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getSag() {
        return sag;
    }

    public void setSag(float sag) {
        this.sag = sag;
    }

    public float getTextureLength() {
        return textureLength;
    }

    public void setTextureLength(float textureLength) {
        this.textureLength = Math.max(0.01f, textureLength);
    }

    public int getSegments() {
        return segments;
    }

    public void setSegments(int segments) {
        this.segments = Math.max(2, segments);
    }
}