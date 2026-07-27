package com.prygin.rope;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class Rope {

    private final UUID entityAId;
    private final UUID entityBId;

    private Identifier texture;
    private float width;
    private float sag;
    private float textureLength;
    private int segments;

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