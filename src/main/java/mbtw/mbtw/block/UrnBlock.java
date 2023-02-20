package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class UrnBlock extends Block implements PotteryBlock {
    public static final IntProperty SOULS = MbtwProperties.SOULS;
    public static final BooleanProperty ATTACHED = MbtwProperties.ATTACHED;
    public static final VoxelShape OUTLINE_SHAPE;
    public static final VoxelShape OUTLINE_SHAPE_UP;

    public UrnBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState()
                .with(SOULS, 0)
                .with(ATTACHED, false)
        );
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(Direction.UP));
        if (state.isOf(Mbtw.MECHANICAL_HOPPER)) {
            return this.getDefaultState().with(ATTACHED, true);
        } else {
            return this.getDefaultState();
        }
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction != Direction.UP) {
            return state;
        }
        if (neighborState.isOf(Mbtw.MECHANICAL_HOPPER) && !state.get(ATTACHED)) {
            return state.with(ATTACHED, true);
        } else if (state.get(ATTACHED)) {
            return state.with(ATTACHED, false);
        }
        return state;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(SOULS);
        stateManager.add(ATTACHED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(ATTACHED)) {
            return OUTLINE_SHAPE_UP;
        } else {
            return OUTLINE_SHAPE;
        }
    }

    static {
        Double[] coords = {5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D};

        OUTLINE_SHAPE = ShapeHelper.createCuboidShapeFromArray(coords);
        OUTLINE_SHAPE_UP = ShapeHelper.createCuboidShapeFromArray(ShapeHelper.translateCuboidCoords(coords, 0, 6, 0));
    }
}
