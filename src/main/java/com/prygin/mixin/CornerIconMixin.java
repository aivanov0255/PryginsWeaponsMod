package com.prygin.mixin;

import com.prygin.Guns;
import com.prygin.item.EffectTrapItem;
import com.prygin.item.MobTrapItem;
import com.prygin.item.TrapItem;
import com.prygin.trap.EffectTrap;
import com.prygin.trap.MobTrap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GuiGraphicsExtractor.class)
public abstract class CornerIconMixin {

    @Inject(
            method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    private void renderCornerIcon(Font font, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (stack.getItem() instanceof EffectTrapItem trapItem && trapItem.getEntityType().getBaseClass().isAssignableFrom(EffectTrap.class)) {
            GuiGraphicsExtractor guiGraphics = (GuiGraphicsExtractor) (Object) this;

            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    Objects.requireNonNull(getEffectIconTexture(trapItem.getEffect())),
                    x, y, 0, 0, 8, 8, 8, 8
            );
        } else if (stack.getItem() instanceof MobTrapItem trapItem) {
            GuiGraphicsExtractor guiGraphics = (GuiGraphicsExtractor) (Object) this;

            Identifier spawnEgg = getSpawneggTexture(trapItem.getMobType());

            if (doesResourceExist(Minecraft.getInstance().getResourceManager(), spawnEgg)) {
                guiGraphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        spawnEgg,
                        x, y, 0, 0, 8, 8, 8, 8
                );
            } else {
                if (trapItem.getMobType() == EntityTypes.TNT) {
                    guiGraphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            Identifier.withDefaultNamespace("textures/block/tnt_side.png"),
                            x, y, 0, 0, 8, 8, 8, 8
                    );
                } else {
                    guiGraphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            getEntityTexture(trapItem.getMobType()),
                            x, y, 0, 0, 8, 8, 8, 8
                    );
                }
            }
        }
    }

    @Unique
    @Nullable
    private static Identifier getEffectIconTexture(@Nullable MobEffect effect) {
        Identifier key = BuiltInRegistries.MOB_EFFECT.getKey(effect);

        if (key == null) return null;

        return Identifier.fromNamespaceAndPath(
                key.getNamespace(),
                "textures/mob_effect/" + key.getPath() + ".png"
        );
    }

    @Unique
    @Nullable
    private static Identifier getSpawneggTexture(@Nullable EntityType type) {
        Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(type);

        if (key == null) return null;

        return Identifier.fromNamespaceAndPath(
                key.getNamespace(),
                "textures/item/" + key.getPath() + "_spawn_egg.png"
        );
    }

    @Unique
    @Nullable
    private static Identifier getEntityTexture(@Nullable EntityType type) {
        Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(type);

        if (key == null) return null;

        return Identifier.fromNamespaceAndPath(
                key.getNamespace(),
                "textures/entity/" + key.getPath() + ".png"
        );
    }

    @Unique
    private static boolean doesResourceExist(ResourceManager resourceManager, Identifier textureId) {
        return resourceManager.getResource(textureId).isPresent();
    }
}