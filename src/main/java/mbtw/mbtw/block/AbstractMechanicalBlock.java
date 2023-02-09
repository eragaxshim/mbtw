package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractMechanicalBlock extends Block implements BlockEntityProvider, MechanicalSink {
    public static final BooleanProperty POWERED = MbtwProperties.POWERED;
    public static final IntProperty MECHANICAL_SINK = MbtwProperties.MECHANICAL_SINK;

    public AbstractMechanicalBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWERED, false).with(MECHANICAL_SINK, 0));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        this.openScreen(world, pos, player);
        return ActionResult.CONSUME;
    }

    protected abstract void openScreen(World world, BlockPos pos, PlayerEntity player);

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(POWERED);
        stateManager.add(MECHANICAL_SINK);
    }

    @Override
    public int getSink(BlockState state) {
        return state.get(MECHANICAL_SINK);
    }

    @Override
    public boolean isPowered(BlockState state) {
        return state.get(POWERED);
    }

    @Override
    public BlockState setAvailablePower(BlockState state, int availablePower) {
        if (availablePower > 0) {
            return state.with(POWERED, true).with(MECHANICAL_SINK, availablePower);
        } else {
            return state.with(POWERED, false);
        }
    }

    @Override
    public BlockState setSink(BlockState state, int sink) {
        return state.with(MECHANICAL_SINK, sink);
    }
}
