package com.prygin.rope;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RopeManager {

    private static final List<Rope> ROPES = new CopyOnWriteArrayList<>();

    private RopeManager() {
    }

    public static Rope createRope(Entity entityA, Entity entityB, Identifier texture) {
        Rope rope = new Rope(entityA, entityB, texture);
        ROPES.add(rope);
        return rope;
    }

    public static Rope createRope(Entity entityA, Entity entityB, Identifier texture,
                                  float width, float sag, float textureLength, int segments) {
        Rope rope = new Rope(entityA, entityB, texture, width, sag, textureLength, segments);
        ROPES.add(rope);
        return rope;
    }

    public static Rope getRopeFromEntity(Entity entity) {
        for (Rope rope : ROPES) {
            if (rope.getEntityAId().equals(entity.getUUID()) || rope.getEntityBId().equals(entity.getUUID())) {
                return rope;
            }
        }

        return null;
    }

    public static void removeRope(Rope rope) {
        ROPES.remove(rope);
    }

    public static void clear() {
        ROPES.clear();
    }

    public static List<Rope> getRopes() {
        return ROPES;
    }
}