package mbtw.mbtw.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

public class StratifiedBlock extends MultiBreakBlock {
    public static final IntProperty STRATIFICATION = IntProperty.of("stratification", 0, 2);

    public StratifiedBlock(FabricBlockSettings settings, int breakingPoint) {
        super(settings, breakingPoint);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(STRATIFICATION);
    }
}
