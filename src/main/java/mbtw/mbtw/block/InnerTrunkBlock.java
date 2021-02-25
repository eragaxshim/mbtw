package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class InnerTrunkBlock extends InnerLogBlock{

    public InnerTrunkBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BreakInterceptable.BROKEN, false).with(PillarBlock.AXIS, Direction.Axis.Y).with(InnerLogBlock.BREAK_LEVEL, 0).with(InnerLogBlock.UP, false).with(InnerLogBlock.DOWN, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BreakInterceptable.BROKEN);
        stateManager.add(PillarBlock.AXIS);
        stateManager.add(InnerLogBlock.BREAK_LEVEL);
        stateManager.add(InnerLogBlock.UP);
        stateManager.add(InnerLogBlock.DOWN);
    }
}
