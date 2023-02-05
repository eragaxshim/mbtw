package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.MillstoneBlockEntity;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MillstoneBlock extends Block implements BlockEntityProvider, MechanicalSink {
    // Only makes sense if powered
    public static final EnumProperty<Direction> POWERED_UP_DOWN = MbtwProperties.POWERED_UP_DOWN;
    public static final BooleanProperty POWERED = MbtwProperties.POWERED;
    public static final IntProperty MECHANICAL_SINK = MbtwProperties.MECHANICAL_SINK;
    public static final int maxSink = 4;

    public static final Set<Direction> VALID_INPUT_FACES = new HashSet<>(Arrays.asList(Direction.UP, Direction.DOWN));
    private static final VoxelShape base = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.);
    private static final VoxelShape middle = Block.createCuboidShape(1.0, 9.0, 1.0, 15.0, 11.0, 15.0);
    private static final VoxelShape top = Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape OUTLINE = VoxelShapes.union(base, middle, top);

    public MillstoneBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWERED, false).with(POWERED_UP_DOWN, Direction.UP));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MillstoneBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            world.setBlockState(pos, state.with(POWERED, !state.get(POWERED)), Block.NOTIFY_ALL);
            return ActionResult.CONSUME;
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(POWERED);
        stateManager.add(POWERED_UP_DOWN);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != Mbtw.MILLSTONE_ENTITY || world.isClient) {
            return null;
        }

        return (world1, pos, state1, millstone) -> MillstoneBlockEntity.serverTick(world1, pos, state1, (MillstoneBlockEntity) millstone);
    }

    @Override
    public boolean addSource(World world, MechanicalVec rotVec, BlockPos sourcePos, BlockState sourceState, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MillstoneBlockEntity) {
            return ((MillstoneBlockEntity) blockEntity).addSource(rotVec, sourcePos, sourceState, pos, state);
        } else {
            return false;
        }
    }

//    @Override
//    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
//
//        super.onStateReplaced(state, world, pos, newState, moved);
//        if (world.isClient) {
//            return;
//        }
//
//        if (!state.isOf(newState.getBlock())) {
//            for (Direction direction : VALID_INPUT_FACES) {
//                world.updateNeighbor();
//            }
//        }
//    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    public int getMaxSink() {
        return maxSink;
    }

    @Override
    public int getSink(BlockState state) {
        return state.get(MECHANICAL_SINK);
    }
}
