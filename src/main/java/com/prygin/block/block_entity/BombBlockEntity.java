package com.prygin.block.block_entity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class BombBlockEntity extends BlockEntity{
    public BombBlockEntity(BlockPos worldPos, BlockState blockState) {
        super(ModBlockEntities.BOMB_BLOCK_ENTITY, worldPos, blockState);
    }
}
