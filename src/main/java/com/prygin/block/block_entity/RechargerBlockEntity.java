package com.prygin.block.block_entity;

import com.prygin.block.RechargerContainerMenu;
import com.prygin.item.Chargable;
import com.prygin.item.CyberCannonItem;
import com.prygin.item.components.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class RechargerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private int timeSinceAddedCharge = 0;

    public RechargerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.RECHARGER, worldPosition, blockState);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(items, slot);
        setChanged();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        items.set(slot, itemStack);
        if (itemStack.getCount() > getMaxStackSize()) {
            itemStack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.recharger");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        super.saveAdditional(output);
    }

    /**
     * Marks the block entity dirty for saving AND pushes an immediate
     * block-update packet to nearby clients, so renderers/other listeners
     * on the client see the current inventory instead of a stale/empty copy.
     */
    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    /**
     * Required override: the default implementation returns an essentially
     * empty tag, so without this override the update packet below carries
     * no actual inventory data. saveWithoutMetadata() routes through
     * saveAdditional(), so this reuses the same serialization as disk saves.
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = saveWithoutMetadata(registries);
        return tag;
    }

    /**
     * Required override: the default implementation returns null, meaning
     * no update packet is ever sent. This tells the game to sync this
     * block entity's data (via getUpdateTag/handleUpdateTag) whenever
     * sendBlockUpdated is called.
     */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new RechargerContainerMenu(containerId, inventory, this);
    }

    int life = 0;

    public static void tick(Level world, BlockPos blockPos, BlockState blockState, RechargerBlockEntity rechargerBlockEntity) {
        rechargerBlockEntity.life++;

        if (rechargerBlockEntity.isEmpty()) return;

        rechargerBlockEntity.timeSinceAddedCharge++;

        if (rechargerBlockEntity.timeSinceAddedCharge < 40) return;
        rechargerBlockEntity.timeSinceAddedCharge = 0;

        ItemStack item = rechargerBlockEntity.getItem(0);
        if (!(item.getItem() instanceof Chargable chargable)) return;

        if (item.get(ModComponents.AMMO) >= chargable.chargingSpeed()) return;

        item.set(ModComponents.AMMO, item.get(ModComponents.AMMO) + 1);
        rechargerBlockEntity.setChanged();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if (!(t instanceof RechargerBlockEntity rechargerBlockEntity)) return;
        tick(level, blockPos, blockState, rechargerBlockEntity);
    }
}