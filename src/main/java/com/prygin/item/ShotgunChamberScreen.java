package com.prygin.item;

import com.prygin.Guns;
import com.prygin.block.RechargerContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ShotgunChamberScreen extends AbstractContainerScreen<ShotgunChamberMenu> {
    private static final Identifier CONTAINER_TEXTURE = Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/gui/container/shotgun_chamber.png");

    private static final int TOP_OVERHANG = 8;

    public ShotgunChamberScreen(ShotgunChamberMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelY = 108;
        this.titleLabelY = -13;
    }

    @Override
    protected void init() {
        super.init();
        this.topPos -= TOP_OVERHANG;
    }

    @Override
    protected boolean hasClickedOutside(double mx, double my, int xo, int yo) {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos, graphics.guiHeight()/2 - 112, 0.0F, 0.0F, this.imageWidth, 224, BACKGROUND_TEXTURE_WIDTH, BACKGROUND_TEXTURE_HEIGHT);
    }
}