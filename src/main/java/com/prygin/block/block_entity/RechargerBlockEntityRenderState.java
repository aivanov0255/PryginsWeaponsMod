package com.prygin.block.block_entity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class RechargerBlockEntityRenderState extends BlockEntityRenderState {
    private boolean hasChargable;

    public final ItemStackRenderState item = new ItemStackRenderState();

    public void setHasChargable(boolean hasChargable) {
        this.hasChargable = hasChargable;
    }

    public boolean hasChargable() {
        return hasChargable;
    }
}