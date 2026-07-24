package com.prygin.rendering;

import com.prygin.zoom.ZoomManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ScopeOverlay {

    private static final Identifier SCOPE_TEXTURE =
            Identifier.fromNamespaceAndPath("guns", "textures/gui/sniper_scope.png");

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("guns", "zoom_overlay"),
                ScopeOverlay::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!ZoomManager.isZooming()) return;

        Minecraft client = Minecraft.getInstance();
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        int size = Math.min(screenWidth, screenHeight);
        int x = (screenWidth - size) / 2;
        int y = (screenHeight - size) / 2;

        graphics.fill(0, 0, screenWidth, y, 0xFF000000);                    // top
        graphics.fill(0, y + size, screenWidth, screenHeight, 0xFF000000);  // bottom
        graphics.fill(0, y, x, y + size, 0xFF000000);                      // left
        graphics.fill(x + size, y, screenWidth, y + size, 0xFF000000);     // right

        // The scope mask: black square with a transparent circular lens baked in
        graphics.blit(RenderPipelines.GUI_TEXTURED, SCOPE_TEXTURE, x, y, 0, 0, size, size, size, size);
    }
}