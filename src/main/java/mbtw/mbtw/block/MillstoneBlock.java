package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.MillstoneBlockEntity;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MillstoneBlock extends Block implements BlockEntityProvider, MechanicalSink {
    // Only makes sense if powered
    public static final EnumProperty<Direction> POWERED_UP_DOWN = MbtwProperties.POWERED_UP_DOWN;
    public static final BooleanProperty POWERED = MbtwProperties.POWERED;

    public static final Set<Direction> VALID_INPUT_FACES = new HashSet<>(Arrays.asList(Direction.UP, Direction.DOWN));

    public MillstoneBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWERED, false).with(POWERED_UP_DOWN, Direction.UP));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MillstoneBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            world.setBlockState(pos, state.with(POWERED, !state.get(POWERED)), Block.NOTIFY_ALL);
            return ActionResult.CONSUME;
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(POWERED);
        stateManager.add(POWERED_UP_DOWN);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != Mbtw.MILLSTONE_ENTITY || world.isClient) {
            return null;
        }

        return (world1, pos, state1, millstone) -> MillstoneBlockEntity.serverTick(world1, pos, state1, (MillstoneBlockEntity) millstone);
    }

    @Override
    public boolean addSource(World world, MechanicalVec rotVec, BlockPos sourcePos, BlockState sourceState, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MillstoneBlockEntity) {
            return ((MillstoneBlockEntity) blockEntity).addSource(rotVec, sourcePos, sourceState, pos, state);
        } else {
            return false;
        }
    }
}
