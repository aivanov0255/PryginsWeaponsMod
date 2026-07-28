package com.prygin.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PopupWallBlock extends Block {
    public static final int MAX_ITERATIONS = 5;

    private static final int DELAY_TICKS = 2;

    public static final IntegerProperty ITERATION = IntegerProperty.create("iteration", 0, MAX_ITERATIONS);

    public static final Property<Direction> FACING = new Property<Direction>("facing", Direction.class) {
        @Override
        public List<Direction> getPossibleValues() {
            return List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        }

        @Override
        public String getName(Direction v) {
            switch (v) {
                case NORTH:
                    return "north";
                case SOUTH:
                    return "south";
                case EAST:
                    return "east";
                case WEST:
                    return "west";
            }
            return "";
        }

        @Override
        public Optional<Direction> getValue(String name) {
            if (name.equals("north")) {
                return Optional.of(Direction.NORTH);
            } else if (name.equals("east")) {
                return Optional.of(Direction.EAST);
            } else if (name.equals("south")) {
                return Optional.of(Direction.SOUTH);
            } else if (name.equals("west")) {
                return Optional.of(Direction.WEST);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public int getInternalIndex(Direction v) {
            switch (v) {
                case NORTH:
                    return 0;
                case SOUTH:
                    return 1;
                case EAST:
                    return 2;
                case WEST:
                    return 3;
            }
            return -1;
        }
    };

    public PopupWallBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(ITERATION, 0)
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ITERATION, FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, DELAY_TICKS);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int currentIteration = state.getValue(ITERATION);

        if (currentIteration < MAX_ITERATIONS) {
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
                    BlockState newState = ModBlocks.TEMPORARY_POPUP_WALL.defaultBlockState()
                            .setValue(TemporaryPopupWall.FACING, facing)
                            .setValue(TemporaryPopupWall.ITERATION, currentIteration + 1);

                    level.setBlock(targetPos, newState, Block.UPDATE_ALL);
                }
            }
        }
    }
}
