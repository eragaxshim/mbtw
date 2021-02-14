package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InterceptBreakBlock extends Block {
    public static final BooleanProperty BROKEN = BooleanProperty.of("broken");

    public InterceptBreakBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BROKEN, false));
    }

    public BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, Item handItem)
    {
        return this.getDefaultState();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BROKEN);
    }
}
