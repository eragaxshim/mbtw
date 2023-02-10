package mbtw.mbtw.block;

import mbtw.mbtw.DynamicMechanicalSource;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.GearboxBlockEntity;
import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.SourceUpdate;
import mbtw.mbtw.util.math.DirectionHelper;
import mbtw.mbtw.util.math.Relative;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GearboxBlock extends Block implements DynamicMechanicalSource, MechanicalSink, BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<Direction> UP_DIRECTION = MbtwProperties.UP_DIRECTION;
    // bitwise flag
    public static final IntProperty BEARING = IntProperty.of("bearing", 0, 31);
    public static final IntProperty SOURCE_BASE = IntProperty.of("source_base", 0, 4);
    // correspond to order of DirectionHelper.Relative
    // max ratio * max power / min ratio cannot be more than max allowed mechanical power
    public static final int[] SOURCE_RATIOS = {0, 1, 2, 0, 0, 0};

    public GearboxBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState()
                .with(FACING, Direction.SOUTH)
                .with(UP_DIRECTION, Direction.EAST)
                .with(BEARING, 0)
                .with(SOURCE_BASE, 1));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction up_relative = Direction.NORTH;
        if (ctx.getPlayer() != null) {
            Direction horizontalFacing = ctx.getPlayer().getHorizontalFacing();
            if (horizontalFacing != Direction.UP && horizontalFacing != Direction.DOWN) {
                up_relative = horizontalFacing;
            }
        }

        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(UP_DIRECTION, up_relative);
    }

    @Override
    public boolean getBearingAtFace(BlockState state, Direction face) {
        Relative relative = DirectionHelper.getRelativeCheckY(state.get(FACING), state.get(UP_DIRECTION), face);
        return (state.get(BEARING) & relative.getBitField()) != 0;
    }

    @Override
    public BlockState setBearingAtFace(BlockState state, Direction face, boolean bearing) {
        Relative relative =  DirectionHelper.getRelativeCheckY(state.get(FACING), state.get(UP_DIRECTION), face);

        int newValue;
        if (bearing) {
            // bitwise or, will always add the specific bit
            newValue = state.get(BEARING) | relative.getBitField();
        } else {
            // bitwise and of state and field to switch ensures we only subtract if it is set
            newValue = state.get(BEARING) - (state.get(BEARING) & relative.getBitField());
        }
        return state.with(BEARING, newValue);
    }

    @Override
    public List<Direction> getOutputFaces(BlockState state) {
        List<Direction> list = new ArrayList<>();
        for (int i = 0; i < SOURCE_RATIOS.length; i++) {
            if (SOURCE_RATIOS[i] > 0) {
                list.add(DirectionHelper.relativeToCheckY(state.get(FACING), state.get(UP_DIRECTION), Relative.byIndex(i)));
            }
        }
        return list;
    }

    @Override
    public int costPerBase(BlockState state, List<Direction> includedFaces) {
        return includedFaces.stream().reduce(0, (t, direction) -> {
            Relative relative = DirectionHelper.getRelativeCheckY(state.get(FACING), state.get(UP_DIRECTION), direction);
            return t+SOURCE_RATIOS[relative.getIndex()];
        }, Integer::sum);
    }

    @Override
    public int getRatioAtFace(BlockState state, Direction face) {
        Relative relative = DirectionHelper.getRelativeCheckY(state.get(FACING), state.get(UP_DIRECTION), face);
        return SOURCE_RATIOS[relative.getIndex()];
    }

    @Override
    public BlockState setSourceBase(BlockState state, int sourceBase) {
        return state.with(SOURCE_BASE, sourceBase);
    }

    @Override
    public int getSourceBase(BlockState state) {
        return state.get(SOURCE_BASE);
    }

    @Override
    public int getAvailableDelivery(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return getAvailablePower(world, state, pos, blockEntity);
    }

    @Override
    public boolean isSourceAtFace(BlockState state, Direction face) {
        Relative relative = DirectionHelper.getRelativeCheckY(state.get(FACING), state.get(UP_DIRECTION), face);
        return SOURCE_RATIOS[relative.getIndex()] > 0;
    }

    @Override
    public int getSourceAtFace(BlockState state, Direction face) {
        int sourceBase = state.get(SOURCE_BASE);
        if (sourceBase > 0) {
            Relative relative = DirectionHelper.getRelativeCheckY(state.get(FACING), state.get(UP_DIRECTION), face);
            return SOURCE_RATIOS[relative.getIndex()] * sourceBase;
        }
        return 0;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        Block block = state.getBlock();
        if (!oldState.isOf(state.getBlock()) && !world.isClient && block instanceof DynamicMechanicalSource source) {
            BlockState updatedState = this.update(world, pos, state, source);
            if (updatedState != state && world.getBlockState(pos) == state) {
                world.setBlockState(pos, updatedState, Block.NOTIFY_ALL);
            }

            for (Direction direction : source.getOutputFaces(state)) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && state.getBlock() instanceof DynamicMechanicalSource source) {
            BlockState updatedState = this.update(world, pos, state, source);
            if (updatedState != state && world.getBlockState(pos) == state) {
                world.setBlockState(pos, updatedState, Block.NOTIFY_ALL);
            }
        }
    }

    public BlockState update(World world, BlockPos pos, BlockState state, DynamicMechanicalSource source) {
        SourceUpdate update = new SourceUpdate(world, pos, state, source);
        BlockState newState = update.updateSourceBase();
        return update.updateBearing(newState);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(FACING);
        stateManager.add(BEARING);
        stateManager.add(SOURCE_BASE);
        stateManager.add(UP_DIRECTION);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        System.out.println("Placed!");
        if (!world.isClient) {
            //Optional<BlockPos> possibleSink = lookForSink(world, pos, state);
        }
    }

    @Override
    public int getMaxSink(BlockState state) {
        return 16;
    }

    @Override
    public int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        if (blockEntity != null) {
            return blockEntity.getSink(world, state, pos, blockEntity);
        }
        if (world.getBlockEntity(pos) instanceof MechanicalSinkBlockEntity mechanicalSinkBlockEntity) {
            return mechanicalSinkBlockEntity.getSink(world, state, pos, mechanicalSinkBlockEntity);
        }
        return 0;
    }

    @Override
    public boolean isPowered(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        if (blockEntity != null) {
            return blockEntity.getAvailablePower(world, state, pos, blockEntity) > 0;
        }
        if (world.getBlockEntity(pos) instanceof MechanicalSinkBlockEntity mechanicalSinkBlockEntity) {
            return mechanicalSinkBlockEntity.getAvailablePower(world, state, pos, mechanicalSinkBlockEntity) > 0;
        }
        return false;
    }

    @Override
    public int getAvailablePower(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        if (blockEntity != null) {
            return blockEntity.getAvailablePower(world, state, pos, blockEntity);
        }
        if (world.getBlockEntity(pos) instanceof MechanicalSinkBlockEntity mechanicalSinkBlockEntity) {
            return mechanicalSinkBlockEntity.getAvailablePower(world, state, pos, mechanicalSinkBlockEntity);
        }
        return 0;
    }

    @Override
    public boolean isSinkAtFace(BlockState state, Direction sinkFace) {
        return sinkFace == state.get(FACING);
    }

    @Override
    public List<Direction> getInputFaces(BlockState state) {
        return List.of(state.get(FACING));
    }

    @Override
    public boolean incongruentInputAllowed(BlockState state) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GearboxBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != Mbtw.GEARBOX_ENTITY || world.isClient) {
            return null;
        }

        return (world1, pos, tickerState, gearbox) -> GearboxBlockEntity.serverTick(world1, pos, tickerState, (GearboxBlockEntity) gearbox);
    }
}
