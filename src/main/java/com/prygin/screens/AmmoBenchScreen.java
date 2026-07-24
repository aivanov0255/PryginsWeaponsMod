package com.prygin.screens;

import com.prygin.Guns;
import com.prygin.menu.AmmoBenchMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AmmoBenchScreen extends AbstractContainerScreen<AmmoBenchMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/gui/container/ammo_bench.png");

    public AmmoBenchScreen(AmmoBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}