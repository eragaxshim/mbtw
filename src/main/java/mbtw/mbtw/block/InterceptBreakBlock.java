package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class InterceptBreakBlock extends Block {
    public static final BooleanProperty BROKEN = BooleanProperty.of("broken");

    public InterceptBreakBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BROKEN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BROKEN);
    }
}
