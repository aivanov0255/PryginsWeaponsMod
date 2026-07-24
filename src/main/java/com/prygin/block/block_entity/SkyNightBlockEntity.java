package com.prygin.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SkyNightBlockEntity extends SkyBlockEntity {
    public SkyNightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SKY_NIGHT_BLOCK_ENTITY, pos, state, 18000);
    }
}
