package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMechanicalBlock extends Block implements BlockEntityProvider, MechanicalSink {
    public static final BooleanProperty POWERED = MbtwProperties.POWERED;

    public AbstractMechanicalBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWERED, false));
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

    @Override
    public int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return MechanicalSinkBlockEntity.blockGetSink(world, pos, blockEntity);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(POWERED);
    }

    @Override
    public boolean isPowered(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return state.get(POWERED);
    }
}
