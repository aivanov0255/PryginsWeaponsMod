package com.prygin.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.prygin.item.DetonatorItem;
import com.prygin.item.components.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangedDetonator extends FaceAttachedHorizontalDirectionalBlock {

    public static final MapCodec<RangedDetonator> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(propertiesCodec()).apply(i, RangedDetonator::new)
    );

    public static final VoxelShape BASE_SHAPE = Shapes.or(
            Block.box(5.0, 1.0, 6.0, 11.0, 2.0, 10.0),
            Block.box(6.0, 2.0, 6.0, 7.0, 4.0, 7.0),
            Block.box(5.0, 2.0, 7.0, 6.0, 3.0, 8.0),
            Block.box(5.0, 2.0, 9.0, 6.0, 3.0, 10.0),
            Block.box(4.5, 0.0, 5.5, 11.5, 1.0, 10.5)
    );

    private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public RangedDetonator(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(FACE, AttachFace.FLOOR)
        );
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(itemStack.getItem() instanceof DetonatorItem)) return InteractionResult.PASS;

        List<BlockPos> positions = itemStack.get(ModComponents.DETONATOR_POSITIONS);

        if (positions.contains(pos)) return InteractionResult.PASS;

        List<BlockPos> mutablePositions = new ArrayList<>(positions);

        mutablePositions.add(pos);

        itemStack.set(ModComponents.DETONATOR_POSITIONS, mutablePositions);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return MAP_CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.computeIfAbsent(state, RangedDetonator::calculateShapeForState);
    }

    private static VoxelShape calculateShapeForState(BlockState state) {
        AttachFace face = state.getValue(FACE);
        Direction facing = state.getValue(FACING);

        VoxelShape shape = BASE_SHAPE;

        switch (face) {
            case CEILING -> shape = flipCeiling(shape);
            case WALL -> shape = rotateToWall(shape);
            case FLOOR -> {}
        }

        return rotateHorizontal(Direction.NORTH, facing, shape);
    }

    private static VoxelShape flipCeiling(VoxelShape shape) {
        VoxelShape[] result = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            result[0] = Shapes.or(result[0], Shapes.box(minX, 1.0 - maxY, minZ, maxX, 1.0 - minY, maxZ));
        });
        return result[0];
    }

    private static VoxelShape rotateToWall(VoxelShape shape) {
        VoxelShape[] result = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            result[0] = Shapes.or(result[0], Shapes.box(minX, minZ, 1.0 - maxY, maxX, maxZ, 1.0 - minY));
        });
        return result[0];
    }

    private static VoxelShape rotateHorizontal(Direction from, Direction to, VoxelShape shape) {
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