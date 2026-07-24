package com.prygin.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SkySunSetBlockEntity extends SkyBlockEntity {
    public SkySunSetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SKY_SUNSET_BLOCK_ENTITY, pos, state, 13000);
    }
}
