package com.prygin.menu;

import com.prygin.block.ModBlocks;
import com.prygin.item.components.ModComponents;
import com.prygin.item.components.ShotgunAmmoProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AmmoBenchMenu extends AbstractContainerMenu {

    private static final int SHELL_SLOT = 0;
    private static final int MODIFIER_SLOT = 1;
    private static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int USE_ROW_SLOT_END = 39;

    private final Container inputContainer = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            AmmoBenchMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer resultContainer = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public AmmoBenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public AmmoBenchMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuTypes.AMMO_BENCH_MENU, containerId);
        this.access = access;
        this.player = playerInventory.player;

        this.addSlot(new Slot(inputContainer, 0, 47, 35));   // shell
        this.addSlot(new Slot(inputContainer, 1, 65, 35));   // modifier
        this.addSlot(new ResultSlot(RESULT_SLOT, 116, 35));  // modified shell

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public void slotsChanged(Container container) {
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }

        ItemStack shellStack = inputContainer.getItem(SHELL_SLOT);
        ItemStack modifierStack = inputContainer.getItem(MODIFIER_SLOT);

        ShotgunAmmoProperties current = shellStack.get(ModComponents.SHOTGUN_AMMO_PROPERTIES);

        if (shellStack.isEmpty() || modifierStack.isEmpty() || current == null) {
            resultContainer.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            return;
        }

        ShotgunAmmoProperties updated = applyModifier(current, modifierStack);
        if (updated == null) {
            // Item in the modifier slot isn't one we recognize.
            resultContainer.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            return;
        }

        // Potions and milk buckets treat the whole batch of shells at once, rather than just one.
        int resultCount = isBatchModifier(modifierStack) ? shellStack.getCount() : 1;

        ItemStack output = shellStack.copyWithCount(resultCount);
        output.set(ModComponents.SHOTGUN_AMMO_PROPERTIES, updated);
        resultContainer.setItem(0, output);

        this.broadcastChanges();
    }

    private static boolean isBatchModifier(ItemStack modifier) {
        return modifier.get(DataComponents.POTION_CONTENTS) != null || modifier.is(Items.MILK_BUCKET);
    }

    @Nullable
    private static ShotgunAmmoProperties applyModifier(ShotgunAmmoProperties props, ItemStack modifier) {
        if (modifier.is(Items.TNT)) {
            return new ShotgunAmmoProperties(
                    props.explodeRange() + 1, props.hitDamage(), props.teleportTargetRandomly(), props.statusEffect());
        }

        if (modifier.is(Items.CHORUS_FRUIT)) {
            return new ShotgunAmmoProperties(
                    props.explodeRange(), props.hitDamage(), true, props.statusEffect());
        }

        if (modifier.is(Items.IRON_INGOT) || modifier.is(Items.GOLD_INGOT)) {
            return new ShotgunAmmoProperties(
                    props.explodeRange(), props.hitDamage() + 1, props.teleportTargetRandomly(), props.statusEffect());
        }

        if (modifier.is(Items.MILK_BUCKET)) {
            return new ShotgunAmmoProperties(
                    props.explodeRange(), props.hitDamage(), props.teleportTargetRandomly(), Optional.empty());
        }

        PotionContents potionContents = modifier.get(DataComponents.POTION_CONTENTS);

        if (potionContents != null) {
            List<MobEffectInstance> effects = potionContents.potion().get().value().getEffects();

            List<MobEffectInstance> joinedEffects = new ArrayList<>();

            if (props.statusEffect().isPresent()) {
                joinedEffects.addAll(props.statusEffect().get());
            }

            joinedEffects.addAll(effects);

            return new ShotgunAmmoProperties(
                    props.explodeRange(), props.hitDamage(), props.teleportTargetRandomly(), Optional.of(joinedEffects)
            );
        }

        return null;
    }

    private static ItemStack getModifierRemainder(ItemStack modifier) {
        PotionContents potionContents = modifier.get(DataComponents.POTION_CONTENTS);

        if (potionContents != null) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (modifier.is(Items.MILK_BUCKET)) {
            return new ItemStack(Items.BUCKET);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.AMMO_BENCH);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputContainer));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            copy = stackInSlot.copy();

            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, copy);
            } else if (index == SHELL_SLOT || index == MODIFIER_SLOT) {
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, SHELL_SLOT, RESULT_SLOT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return copy;
    }

    private class ResultSlot extends Slot {
        public ResultSlot(int index, int x, int y) {
            super(resultContainer, index - RESULT_SLOT, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            ItemStack modifierStack = inputContainer.getItem(MODIFIER_SLOT);
            ItemStack remainder = getModifierRemainder(modifierStack);

            // Potions and milk buckets consume the whole shell stack; every other modifier consumes just one shell.
            int shellsToConsume = isBatchModifier(modifierStack)
                    ? inputContainer.getItem(SHELL_SLOT).getCount()
                    : 1;

            inputContainer.removeItem(SHELL_SLOT, shellsToConsume);
            inputContainer.removeItem(MODIFIER_SLOT, 1);

            if (!remainder.isEmpty()) {
                ItemStack currentModifierSlot = inputContainer.getItem(MODIFIER_SLOT);
                if (currentModifierSlot.isEmpty()) {
                    inputContainer.setItem(MODIFIER_SLOT, remainder);
                } else if (ItemStack.isSameItemSameComponents(currentModifierSlot, remainder)
                        && currentModifierSlot.getCount() + remainder.getCount() <= currentModifierSlot.getMaxStackSize()) {
                    currentModifierSlot.grow(remainder.getCount());
                } else {
                    player.getInventory().placeItemBackInInventory(remainder);
                }
            }

            super.onTake(player, stack);
        }
    }
}