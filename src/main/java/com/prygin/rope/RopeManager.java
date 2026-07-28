package com.prygin.rope;

import com.prygin.Guns;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RopeManager {

    private static final List<Rope> ROPES = new CopyOnWriteArrayList<>();

    private RopeManager() {
    }

    public static Rope createRopeNonSync(Rope rope) {
        Rope newRope = new Rope(rope);
        ROPES.add(newRope);

        return newRope;
    }

    public static void removeRopeNonSync(Rope rope) {
        ROPES.remove(rope);
    }

    public static Rope createRope(Rope rope) {
        Rope newRope = new Rope(rope);
        ROPES.add(newRope);

        ClientboundRopePayload payload = new ClientboundRopePayload(rope, ClientboundRopePayload.CreateType.CREATE);

        for (ServerPlayer player : PlayerLookup.all(Guns.serverInstance)) {
            ServerPlayNetworking.send(player, payload);
        }

        return newRope;
    }

    public static Rope createRope(Entity entityA, Entity entityB, Identifier texture) {
        Rope rope = new Rope(entityA, entityB, texture);
        ROPES.add(rope);

        ClientboundRopePayload payload = new ClientboundRopePayload(rope, ClientboundRopePayload.CreateType.CREATE);

        for (ServerPlayer player : PlayerLookup.all(Guns.serverInstance)) {
            ServerPlayNetworking.send(player, payload);
        }

        return rope;
    }

    public static Rope createRope(Entity entityA, Entity entityB, Identifier texture,
                                  float width, float sag, float textureLength, int segments) {
        Rope rope = new Rope(entityA, entityB, texture, width, sag, textureLength, segments);
        ROPES.add(rope);

        ClientboundRopePayload payload = new ClientboundRopePayload(rope, ClientboundRopePayload.CreateType.CREATE);

        for (ServerPlayer player : PlayerLookup.all(Guns.serverInstance)) {
            ServerPlayNetworking.send(player, payload);
        }

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

        ClientboundRopePayload payload = new ClientboundRopePayload(rope, ClientboundRopePayload.CreateType.REMOVE);

        for (ServerPlayer player : PlayerLookup.all(Guns.serverInstance)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void clear() {
        ROPES.clear();
    }

    public static List<Rope> getRopes() {
        return ROPES;
    }
}