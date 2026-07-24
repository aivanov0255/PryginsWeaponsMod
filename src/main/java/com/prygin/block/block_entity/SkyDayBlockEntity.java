package com.prygin.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SkyDayBlockEntity extends SkyBlockEntity {
    public SkyDayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SKY_DAY_BLOCK_ENTITY, pos, state, 6000);
    }
}
