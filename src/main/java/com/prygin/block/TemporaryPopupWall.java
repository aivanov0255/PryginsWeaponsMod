package com.prygin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class TemporaryPopupWall extends Block {
    public static final int MAX_ITERATIONS = PopupWallBlock.MAX_ITERATIONS;
    public static final IntegerProperty ITERATION = PopupWallBlock.ITERATION;
    public static final Property<Direction> FACING = PopupWallBlock.FACING;

    private static final int DELAY_TICKS = 2;

    private static final int LIFETIME_TICKS = 500;

    private static final int BREAK_STAGES = 10;

    private static final int STAGE_INTERVAL = Math.max(1, LIFETIME_TICKS / BREAK_STAGES);

    private static final IntegerProperty AGE = IntegerProperty.create("age", 0, BREAK_STAGES);

    public TemporaryPopupWall(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(ITERATION, 0)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(AGE, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ITERATION, FACING, AGE);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide() && oldState.getBlock() != state.getBlock()) {
            level.scheduleTick(pos, this, DELAY_TICKS);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int currentIteration = state.getValue(ITERATION);
        int age = state.getValue(AGE);

        if (age == 0 && currentIteration < MAX_ITERATIONS) {
            Direction facing = state.getValue(FACING);

            Direction[] perpendicularDirections = new Direction[] {
                    Direction.UP,
                    Direction.DOWN,
                    facing.getClockWise(),
                    facing.getCounterClockWise()
            };

            for (Direction dir : perpendicularDirections) {
                BlockPos targetPos = pos.relative(dir);

                if (level.isEmptyBlock(targetPos)) {
                    BlockState newState = this.defaultBlockState()
                            .setValue(FACING, facing)
                            .setValue(ITERATION, currentIteration + 1);

                    level.setBlock(targetPos, newState, Block.UPDATE_ALL);
                }
            }
        }

        tickBreaking(state, level, pos);
    }

    private void tickBreaking(BlockState state, ServerLevel level, BlockPos pos) {
        int age = state.getValue(AGE);
        int breakerId = getBreakerId(pos);

        if (age >= BREAK_STAGES) {
            // Fully cracked - clear the overlay and break the block.
            level.destroyBlockProgress(breakerId, pos, -1);
            level.removeBlock(pos, false);
            level.levelEvent(2001, pos, Block.getId(state));
            return;
        }

        level.destroyBlockProgress(breakerId, pos, age);
        level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_ALL);
        level.scheduleTick(pos, this, STAGE_INTERVAL);
    }

    private static int getBreakerId(BlockPos pos) {
        return pos.hashCode();
    }
}