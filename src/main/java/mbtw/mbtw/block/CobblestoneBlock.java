package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CobblestoneBlock extends Block {
    public static final IntProperty BROKEN = IntProperty.of("broken", 0, 10);

    public CobblestoneBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BROKEN, 0));
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        int i = state.get(BROKEN);
        if (state.get(BROKEN) < 5){
            world.setBlockState(pos, state.with(BROKEN, i + 1), 2);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BROKEN);
    }
}
