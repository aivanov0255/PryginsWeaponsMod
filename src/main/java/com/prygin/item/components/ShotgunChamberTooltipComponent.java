package com.prygin.item.components;

import com.prygin.Guns;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShotgunChamberTooltipComponent implements ClientTooltipComponent {
    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 4;

    private final ShotgunChamberTooltip data;

    public ShotgunChamberTooltipComponent(ShotgunChamberTooltip data) {
        this.data = data;
    }

    @Override
    public int getHeight(Font font) {
        return 75;
    }

    @Override
    public int getWidth(Font font) {
        return 72;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor guiGraphics) {
        List<ItemStack> shells = data.shells();

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(Guns.MOD_ID, "textures/tooltip/shotgun_tooltip_background.png"), x, y, 0, 0, 72, 72, 72, 72);

        for (int i = 0; i < shells.size(); i++) {
            ItemStack shell = shells.get(i);
            if (shell.isEmpty()) continue;

            int col = i % COLUMNS;
            int row = i / COLUMNS;

            int slotX = x + col * SLOT_SIZE + 1;
            int slotY = y + row * SLOT_SIZE + 1;

            guiGraphics.item(shell, slotX, slotY);
            guiGraphics.itemDecorations(font, shell, slotX, slotY,
                    shell.getCount() == 1 ? "" : String.valueOf(shell.getCount()));
        }
    }
}