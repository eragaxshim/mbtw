package mbtw.mbtw.block;

import mbtw.mbtw.util.math.DirectionHelper;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class GearboxBlock extends Block implements MechanicalSource {
    public static final Direction OUT_DIRECTION = Direction.EAST;
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty BEARING_UP = BooleanProperty.of("bearing_up");
    public static final IntProperty SOURCE_UP = IntProperty.of("source_up", 0, 4);
    public static final BooleanProperty BEARING_DOWN = BooleanProperty.of("bearing_down");
    public static final IntProperty SOURCE_DOWN = IntProperty.of("source_down", 0, 4);

    public GearboxBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState()
                .with(FACING, Direction.SOUTH)
                .with(BEARING_UP, false)
                .with(SOURCE_UP, 2)
                .with(BEARING_DOWN, false)
                .with(SOURCE_DOWN, 2));
    }

    @Override
    public boolean getBearingAtFace(BlockState state, Direction face) {
        Direction facing = state.get(FACING);
        Direction up = DirectionHelper.relativeTo(facing, DirectionHelper.Relative.UP);

        if (face == up) {
            return state.get(BEARING_UP);
        } else if (face == up.getOpposite()) {
            return state.get(BEARING_DOWN);
        } else {
            return false;
        }
    }

    @Override
    public boolean isSourceAtFace(BlockState state, Direction face) {
        Direction facing = state.get(FACING);
        Direction up = DirectionHelper.relativeTo(facing, DirectionHelper.Relative.UP);

        return face == up || face == up.getOpposite();
    }

    @Override
    public int getSourceAtFace(BlockState state, Direction face) {
        Direction facing = state.get(FACING);
        Direction up = DirectionHelper.relativeTo(facing, DirectionHelper.Relative.UP);

        if (face == up) {
            return state.get(SOURCE_UP);
        } else if (face == up.getOpposite()) {
            return state.get(SOURCE_DOWN);
        } else {
            return 0;
        }
    }

    @Override
    public List<MechanicalVec> getOutVecs(BlockPos sourcePos, BlockState sourceState) {
        return List.of(new MechanicalVec(OUT_DIRECTION));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean notify) {
        if (world.isClient) {
            return;
        }

        if (neighborBlock instanceof MechanicalConnector connector) {
            Vec3i outVec = neighborPos.subtract(pos);
            if (MechanicalVec.notUniDirectional(neighborPos.subtract(pos))) {
                return;
            }
            Direction incomingFace = (new MechanicalVec(outVec)).getDirection();
            BlockState neighborState = world.getBlockState(neighborPos);
            if (!isSourceAtFace(state, incomingFace) || !connector.isOutputAtFace(neighborState, incomingFace.getOpposite())) {
                return;
            }
            if (connector.getSource(neighborState) > 0) {
                // This means connector is inputting into source, which is not allowed
                world.breakBlock(neighborPos, false);
            }
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(FACING);
        stateManager.add(BEARING_UP);
        stateManager.add(BEARING_DOWN);
        stateManager.add(SOURCE_UP);
        stateManager.add(SOURCE_DOWN);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        System.out.println("Placed!");
        if (!world.isClient) {
            //Optional<BlockPos> possibleSink = lookForSink(world, pos, state);
        }
    }
}
