package com.prygin.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkyBlockEntity extends BlockEntity {
    int time;
    
    public SkyBlockEntity(BlockEntityType type, BlockPos pos, BlockState state, int time) {
        super(type, pos, state);

        this.time = time;
    }
}
