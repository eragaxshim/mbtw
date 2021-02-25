package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InnerTrunkBlock extends InnerLogBlock{

    public InnerTrunkBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(InterceptBreakBlock.BROKEN, false).with(InnerLogBlock.BREAK_LEVEL, 0).with(InnerLogBlock.UP, false).with(InnerLogBlock.DOWN, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(InterceptBreakBlock.BROKEN);
        stateManager.add(InnerLogBlock.BREAK_LEVEL);
        stateManager.add(InnerLogBlock.UP);
        stateManager.add(InnerLogBlock.DOWN);
    }
}
