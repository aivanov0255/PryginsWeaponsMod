package com.prygin.common;

import com.prygin.Guns;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public final class ModAttachments {

    public static final AttachmentType<Boolean> EMERGING = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "zombie_emerging"),
            builder -> builder.syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<Integer> EMERGE_TICKS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "zombie_emerge_ticks"),
            builder -> builder.syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.all())
    );

    private ModAttachments() {}

    public static void init() {}
}