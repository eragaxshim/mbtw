package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InnerLogBlock extends InterceptBreakBlock {
    protected static final VoxelShape SHAPE_B1;
    protected static final VoxelShape SHAPE_B2;
    protected static final VoxelShape SHAPE_B3;
    protected static final VoxelShape SHAPE_B4;
    public static final IntProperty BREAK_LEVEL = IntProperty.of("break_level", 0, 4);
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;

    public InnerLogBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(InterceptBreakBlock.BROKEN, false).with(BREAK_LEVEL, 0).with(UP, false).with(DOWN, false));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int b = state.get(BREAK_LEVEL);
        boolean connected = state.get(UP) && state.get(DOWN);
        if (b == 0)
        {
            return VoxelShapes.fullCube();
        }
        else if (connected || b == 1)
        {
            return SHAPE_B1;
        }
        else {
            switch (b)
            {
                case 2:
                    return SHAPE_B2;
                case 3:
                    return SHAPE_B3;
                case 4:
                    return SHAPE_B4;
            }
            return VoxelShapes.fullCube();
        }
    }

    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, ItemStack handStack)
    {
        int b = state.get(BREAK_LEVEL);
        if (b+1 > 4)
        {
            return state.with(InterceptBreakBlock.BROKEN, true);
        }
        else {
            return state.with(BREAK_LEVEL, b+1);
        }
    }

    static {
        SHAPE_B1 = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
        SHAPE_B2 = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        SHAPE_B3 = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
        SHAPE_B4 = Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
        UP = Properties.UP;
        DOWN = Properties.DOWN;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(InterceptBreakBlock.BROKEN);
        stateManager.add(BREAK_LEVEL);
        stateManager.add(UP);
        stateManager.add(DOWN);
    }
}
