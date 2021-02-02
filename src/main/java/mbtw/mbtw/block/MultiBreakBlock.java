package mbtw.mbtw.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiBreakBlock extends Block {
    public static final IntProperty BROKEN = IntProperty.of("broken", 0, 9);
    public final int breakingPoint;

    public MultiBreakBlock(FabricBlockSettings settings, int breakingPoint) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BROKEN, 0));
        this.breakingPoint = breakingPoint;
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        int i = state.get(BROKEN);

        if (state.get(BROKEN) < breakingPoint){
            world.setBlockState(pos, state.with(BROKEN, i + 1), 2);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BROKEN);
    }
}