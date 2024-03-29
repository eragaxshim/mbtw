package mbtw.mbtw.block;

import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static mbtw.mbtw.block.ShapeHelper.createCuboidShapeFromArray;
import static mbtw.mbtw.block.ShapeHelper.rotateCuboidCoords;

public class AxleBlock extends PillarBlock implements MechanicalConnector {
    protected static final VoxelShape SHAPE;
    protected static final VoxelShape SHAPE_X;
    protected static final VoxelShape SHAPE_Z;

    // true it faces in positive axis direction, negative in negative axis direction
    public static final BooleanProperty INPUT_FACE = MbtwProperties.AXIS_DIRECTION;
    public static final BooleanProperty BEARING_LOAD = MbtwProperties.BEARING_LOAD;
    public static final IntProperty MECHANICAL_SOURCE = MbtwProperties.MECHANICAL_SOURCE;
    public static final IntProperty MECHANICAL_SINK = MbtwProperties.MECHANICAL_SINK;

    public AxleBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState()
                .with(INPUT_FACE, false)
                .with(BEARING_LOAD, false)
                .with(MECHANICAL_SOURCE, 0)
                .with(MECHANICAL_SINK, 0));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Direction.AxisDirection axisDirection = getAxisDirection(state);
        Direction newDirection = rotation.rotate(Direction.from(state.get(PillarBlock.AXIS), axisDirection));
        boolean newFace = newDirection.getDirection() == Direction.AxisDirection.POSITIVE;
        return changeRotation(state, rotation).with(INPUT_FACE, newFace);
    }

    public static BlockState switchInputOutput(BlockState state) {
        return state.with(INPUT_FACE, !state.get(INPUT_FACE));
    }

    public static Direction.AxisDirection getAxisDirection(BlockState state) {
        return state.get(INPUT_FACE) ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
    }

    public static Direction getInputFace(BlockState state) {
        return Direction.from(state.get(PillarBlock.AXIS), getAxisDirection(state));
    }

    public static boolean inputOrOutput(BlockState state, Direction direction) {
        Direction inputFace = getInputFace(state);
        return inputFace == direction || inputFace.getOpposite() == direction;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(PillarBlock.AXIS)) {
            case X -> SHAPE_X;
            default -> SHAPE;
            case Z -> SHAPE_Z;
        };
    }

    public @Nullable BlockState update(World world, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.get(PillarBlock.AXIS);
        Direction toPosNeighbor = Direction.from(axis, Direction.AxisDirection.POSITIVE);

        BlockPos frontPos = pos.offset(toPosNeighbor);
        BlockState front = world.getBlockState(frontPos);
        BlockPos backPos = pos.offset(toPosNeighbor.getOpposite());
        BlockState back = world.getBlockState(backPos);

        MechanicalUpdate frontUpdate = getMechanicalUpdate(world, state, front, frontPos, toPosNeighbor);
        MechanicalUpdate backUpdate = getMechanicalUpdate(world, state, back, backPos, toPosNeighbor.getOpposite());

        if ((frontUpdate.getNewState() == state && backUpdate.getNewState() == state)) {
            return state;
        }

        if (frontUpdate.isCompatibleWith(backUpdate)) {
            return frontUpdate.mergeState(backUpdate);
        } else {
            return null;
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        Block block = state.getBlock();
        if (!oldState.isOf(state.getBlock()) && !world.isClient && block instanceof MechanicalConnector) {
            BlockState updatedState = this.update(world, pos, state);
            if (updatedState == null) {
                world.breakBlock(pos, false);
            } else if (updatedState != state && world.getBlockState(pos) == state) {
                world.setBlockState(pos, updatedState, Block.NOTIFY_ALL);
            }

            Direction.Axis axis = state.get(PillarBlock.AXIS);
            Direction toPosNeighbor = Direction.from(axis, Direction.AxisDirection.POSITIVE);

            world.updateNeighborsAlways(pos.offset(toPosNeighbor), this);
            world.updateNeighborsAlways(pos.offset(toPosNeighbor.getOpposite()), this);
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && state.getBlock() instanceof MechanicalConnector) {
            BlockState updatedState = this.update(world, pos, state);
            if (updatedState == null) {
                world.breakBlock(pos, false);
            } else if (updatedState != state && world.getBlockState(pos) == state) {
                world.setBlockState(pos, updatedState, Block.NOTIFY_ALL);
            }
        }
    }

//    @Override
//    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//
//    }

    // This assumes the neighbor is in the axis of the axle
    public static MechanicalUpdate getMechanicalUpdate(World world, BlockState state, BlockState neighborState, BlockPos neighborPos, Direction toNeighbor) {
        Block neighbor = neighborState.getBlock();
        if (neighbor instanceof MechanicalSink sink && sink.isSinkAtFace(neighborState, toNeighbor.getOpposite())) {
            return stateFromSink(world, state, neighborState, neighborPos, sink, toNeighbor);
        } else if (neighborState.getBlock() instanceof MechanicalSource source && source.isSourceAtFace(neighborState, toNeighbor.getOpposite())) {
            return stateFromSource(world, state, neighborState, neighborPos, source, toNeighbor);
        } else if (neighborState.getBlock() instanceof MechanicalConnector connector && (connector.isOutputAtFace(neighborState, toNeighbor.getOpposite()) || connector.isOutputAtFace(neighborState, toNeighbor))) {
            return stateFromConnector(state, neighborState, connector, toNeighbor);
        } else if (getInputFace(state) == toNeighbor && (state.get(MECHANICAL_SOURCE) > 0 || state.get(BEARING_LOAD))) {
            BlockState returnState = state.with(MECHANICAL_SOURCE, 0).with(BEARING_LOAD, false);
            MechanicalUpdate update = new MechanicalUpdate(state);
            update.addProperty(MECHANICAL_SOURCE);
            update.addProperty(BEARING_LOAD);
            return update.withState(returnState);
        } else if (getInputFace(state).getOpposite() == toNeighbor && state.get(MECHANICAL_SINK) > 0) {
            BlockState returnState = state.with(MECHANICAL_SINK, 0).with(BEARING_LOAD, false);
            MechanicalUpdate update = new MechanicalUpdate(state);
            update.addProperty(MECHANICAL_SINK);
            update.addProperty(BEARING_LOAD);
            return update.withState(returnState);
        } else {
            return new MechanicalUpdate(state).withState(state);
        }
    }

//    @Override
//    public BlockState getStateForNeighborUpdate(BlockState state, Direction incomingFace, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
//        if (inputOrOutput(state, incomingFace)) {
//            MechanicalUpdate update = getMechanicalUpdate(state, neighborState, incomingFace);
//            if (!world.isClient()) {
//                System.out.println("");
//            }
//
//
//            return update.getNewState();
//        }
//
//        return state;
//    }

    static {
        Double[] coordsB1 = {6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D};

        SHAPE = createCuboidShapeFromArray(coordsB1);
        SHAPE_X = createCuboidShapeFromArray(rotateCuboidCoords(coordsB1, 1));
        SHAPE_Z = createCuboidShapeFromArray(rotateCuboidCoords(coordsB1, 2));
    }

    // This assumes the source outputs in axis of axle
    public static MechanicalUpdate stateFromSource(World world, BlockState state, BlockState sourceState, BlockPos sourcePos, MechanicalSource source, Direction toSource) {
        MechanicalUpdate update = new MechanicalUpdate(state);
        int sourceSource = source.getSourceAtFace(sourceState, toSource.getOpposite());
        int selfSource = state.get(MECHANICAL_SOURCE);
        boolean sourceBearing = source.getBearingAtFace(world, sourceState, sourcePos, null, toSource.getOpposite());
        boolean selfBearing = state.get(BEARING_LOAD);

        BlockState newState = state;
        // We always want a source to make it change direction
        update.addProperty(INPUT_FACE);
        if (sourceSource > 0) {
            if (getInputFace(state) != toSource) {
                newState = switchInputOutput(newState);
            }
        }

        if (sourceSource == selfSource && sourceBearing == selfBearing) {
            return update.withState(newState);
        }
        // In this case there is a change
        // Only valid with this input face
        // Ensure input face also is correct
        if (getInputFace(newState) != toSource) {
            newState = switchInputOutput(newState);
        }

        if (sourceSource != selfSource) {
            newState = newState.with(MECHANICAL_SOURCE, sourceSource);
            update.addProperty(MECHANICAL_SOURCE);
        }
        if (sourceBearing != selfBearing) {
            newState = newState.with(BEARING_LOAD, sourceBearing);
            update.addProperty(BEARING_LOAD);
        }
        return update.withState(newState);
    }

    // This assumes the connector outputs into axle
    public static MechanicalUpdate stateFromConnector(BlockState state, BlockState cState, MechanicalConnector connector, Direction toConnector) {
        MechanicalUpdate update = new MechanicalUpdate(state);
        Direction inputFace = getInputFace(state);
        int cSource = connector.getSource(cState);
        // We only switch direction for an active source, not for sink
        // If there is a sink it should only be nonzero if there is a nonzero source
        if (state.get(MECHANICAL_SOURCE) == 0 && cSource > 0) {
            if (toConnector != getInputFace(state)) {
                inputFace = inputFace.getOpposite();
                update.updateState(INPUT_FACE, !state.get(INPUT_FACE));
            } else {
                update.addProperty(INPUT_FACE);
            }
        }

        if (toConnector == inputFace) {
            update.updateState(BEARING_LOAD, connector.getBearing(cState), true);
            update.updateState(MECHANICAL_SOURCE, cSource, true);
        } else if (toConnector == inputFace.getOpposite()) {
            update.updateState(MECHANICAL_SINK, connector.getSink(cState), true);
        }
        return update;
    }

    // This assumes the source sinks in axis of axle
    public static MechanicalUpdate stateFromSink(World world, BlockState state, BlockState sinkState, BlockPos sinkPos, MechanicalSink sink, Direction toSink) {
        MechanicalUpdate update = new MechanicalUpdate(state);
        int sinkSink = sink.getSink(world, sinkState, sinkPos, null);
        int selfSink = state.get(MECHANICAL_SINK);

        BlockState newState = state;
        // If sink zero and this sink nonzero, switch direction
        // Since in this case there cannot be a source
        if (getInputFace(state).getOpposite() != toSink && sinkSink > 0) {
            newState = switchInputOutput(newState);
            update.addProperty(INPUT_FACE);
        }

        // If sink request is not equal, change our sink request
        if (sinkSink != selfSink) {
            update.addProperty(INPUT_FACE);
            boolean selfBearing = state.get(BEARING_LOAD);

            // If requesting more, temporarily it must set bearing load to false and then wait for it to be
            // re-activated
            if (sinkSink > selfSink && selfBearing) {
                newState = newState.with(BEARING_LOAD, false);
                update.addProperty(BEARING_LOAD);
            }
            newState = newState.with(MECHANICAL_SINK, sinkSink);
            update.addProperty(MECHANICAL_SINK);
        }
        return update.withState(newState);
    }

    @Override
    public BlockState withSink(BlockState connectorState, int newSink) {
        return connectorState.with(MECHANICAL_SINK, newSink);
    }

    @Override
    public int getSource(BlockState connectorState) {
        return connectorState.get(MECHANICAL_SOURCE);
    }

    @Override
    public boolean getBearing(BlockState connectorState) {
        return connectorState.get(BEARING_LOAD);
    }

    @Override
    public int getSink(BlockState connectorState) {
        return connectorState.get(MECHANICAL_SINK);
    }

    @Override
    public boolean isOutputAtFace(BlockState connectorState, Direction outputFace) {
        return getInputFace(connectorState).getOpposite() == outputFace;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(INPUT_FACE);
        stateManager.add(BEARING_LOAD);
        stateManager.add(MECHANICAL_SINK);
        stateManager.add(MECHANICAL_SOURCE);
    }
}
