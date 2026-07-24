package com.prygin.item;

import com.prygin.item.components.ModComponents;
import com.prygin.menu.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class ShotgunChamberMenu extends AbstractContainerMenu {
    private static final int SLOTS_ROWS = 4;
    private static final int SLOTS_COLUMNS = 4;
    private static final int SLOTS_COUNT = SLOTS_ROWS * SLOTS_COLUMNS;

    private static final int CONTAINER_START = 0;
    private static final int CONTAINER_END = SLOTS_COUNT;
    private static final int INVENTORY_START = CONTAINER_END;
    private static final int INVENTORY_END = INVENTORY_START + Inventory.INVENTORY_SIZE;

    // Centered for a 176px-wide texture; grid is 72px wide -> (176-72)/2 = 52
    private static final int GRID_START_X = 52;
    private static final int GRID_START_Y = 0;
    private static final int GRID_END_Y = GRID_START_Y + (SLOTS_ROWS * 18); // 89

    private static final int INVENTORY_START_X = 8;
    private static final int INVENTORY_START_Y = GRID_END_Y + 49; // 103

    private final ItemStack stack;
    private final Container container;

    public ShotgunChamberMenu(int id, Inventory inv) {
        this(id, inv, inv.getSelectedItem());
    }

    public ShotgunChamberMenu(int containerId, Inventory inventory, ItemStack item) {
        super(ModMenuTypes.SHOTGUN_CHAMBER, containerId);
        this.stack = item;
        this.container = new SimpleContainer(SLOTS_COUNT);

        List<ItemStack> savedItems = item.getOrDefault(ModComponents.SHOTGUN_CHAMBER, List.of());
        for (int i = 0; i < Math.min(savedItems.size(), SLOTS_COUNT); i++) {
            this.container.setItem(i, savedItems.get(i).copy());
        }

        this.addSlots();
        this.addStandardInventorySlots(inventory, INVENTORY_START_X, INVENTORY_START_Y);
    }

    private void addSlots() {
        int slotIndex = 0;
        for (int row = 0; row < SLOTS_ROWS; row++) {
            for (int col = 0; col < SLOTS_COLUMNS; col++) {
                int x = GRID_START_X + (col * 18);
                int y = GRID_START_Y + (row * 18);
                this.addSlot(new Slot(container, slotIndex, x, y));
                slotIndex++;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();

        if (slotIndex < CONTAINER_END) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stack, CONTAINER_START, CONTAINER_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem().is(this.stack.getItem())
                || player.getOffhandItem().is(this.stack.getItem());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        List<ItemStack> finalItems = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (!itemStack.isEmpty()) {
                finalItems.add(itemStack.copy());
            }
        }

        this.stack.set(ModComponents.SHOTGUN_CHAMBER, finalItems);
    }
}