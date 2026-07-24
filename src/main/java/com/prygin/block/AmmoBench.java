package com.prygin.block;

import com.prygin.menu.AmmoBenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AmmoBench extends Block {

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

    // Base shape facing NORTH
    private static final VoxelShape SHAPE_NORTH = Shapes.join(
            Block.box(3.0, 11.0, 16.0, 5.0, 13.0, 17.0),
            Shapes.or(
                    Block.box(2.67024, -1.45287, 0.0, 4.67024, 8.54713, 2.0),
                    Block.box(2.67024, -1.45287, 14.0, 4.67024, 8.54713, 16.0),
                    Block.box(11.31974, -1.52899, 14.0, 13.31974, 8.47101, 16.0),
                    Block.box(11.31974, -1.52899, 0.0, 13.31974, 8.47101, 2.0),
                    Block.box(3.0, 5.0, 1.0, 13.0, 6.0, 15.0),
                    Block.box(1.0, 9.0, -1.0, 15.0, 11.0, 17.0),
                    Block.box(1.0, 11.0, -1.0, 3.0, 16.0, 17.0),
                    Block.box(2.23984, 6.04349, 0.0, 4.23984, 8.04349, 16.0),
                    Block.box(3.0, 11.0, -1.0, 5.0, 13.0, 0.0),
                    Block.box(9.09996, 11.0, 8.80797, 12.09996, 12.0, 9.80797),
                    Block.box(10.16331, 11.0, 2.19436, 13.16331, 12.0, 3.19436),
                    Block.box(9.66331, 11.0, 3.06038, 12.66331, 12.0, 4.06038),
                    Block.box(6.73767, 12.0, 1.47749, 9.73767, 13.0, 2.47749),
                    Block.box(5.07435, 11.0, 11.20507, 8.07435, 14.0, 14.20507),
                    Block.box(10.70665, 12.5, 10.65448, 11.70665, 13.25, 11.65448),
                    Block.box(10.45665, 13.25, 10.40448, 11.95665, 13.75, 11.90448),
                    Block.box(10.20665, 11.0, 10.15448, 12.20665, 12.5, 12.15448),
                    Block.box(10.45665, 11.25, 10.40448, 11.95665, 12.25, 11.90448),
                    Block.box(13.77456, 13.0, 9.23034, 14.77456, 14.0, 10.23034),
                    Block.box(13.52456, 13.75, 8.98034, 15.02456, 14.25, 10.48034),
                    Block.box(13.27456, 11.0, 8.73034, 15.27456, 13.0, 10.73034),
                    Block.box(13.52456, 11.25, 8.98034, 15.02456, 12.75, 10.48034)
            ),
            BooleanOp.OR
    );

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPES.put(direction, rotateShape(Direction.EAST, direction, SHAPE_NORTH));
        }
    }

    private static final Component CONTAINER_TITLE = Component.translatable("container.ammobench");

    public AmmoBench(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPE_NORTH);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        MenuProvider menuProvider = state.getMenuProvider(level, pos);
        if (menuProvider != null) {
            player.openMenu(menuProvider);
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, playerInventory, player) ->
                        new AmmoBenchMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE
        );
    }

    /**
     * Rotates a VoxelShape from a source direction to a target direction around (8,8,8).
     */
    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.or(buffer[1], Shapes.box(
                        1.0 - maxZ, minY, minX,
                        1.0 - minZ, maxY, maxX
                ));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
}