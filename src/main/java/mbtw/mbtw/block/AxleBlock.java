package mbtw.mbtw.block;

import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
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

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState baseState = super.getPlacementState(ctx);
        if (baseState == null) {
            return null;
        }
        BlockPos pos = ctx.getBlockPos();
        BlockState front = ctx.getWorld().getBlockState(pos.offset(baseState.get(PillarBlock.AXIS), 1));
        BlockState back = ctx.getWorld().getBlockState(pos.offset(baseState.get(PillarBlock.AXIS), -1));


        return baseState;
    }

    public static boolean isIncompatibleUpdate(BlockState oldState, BlockState state1, BlockState state2) {

    }

    // This assumes the neighbor is in the axis of the axle
    public static BlockState getMechanicalUpdate(BlockState state, BlockState neighborState, Direction toNeighbor) {
        Block neighbor = neighborState.getBlock();
        if (neighbor instanceof MechanicalSink sink && sink.isSinkAtFace(neighborState, toNeighbor.getOpposite())) {
            return stateFromSink(state, neighborState, sink, toNeighbor);
        } else if (neighborState.getBlock() instanceof MechanicalSource source && source.isSourceAtFace(neighborState, toNeighbor.getOpposite())) {
            return stateFromSource(state, neighborState, source, toNeighbor);
        } else if (neighborState.getBlock() instanceof MechanicalConnector connector && connector.isOutputAtFace(neighborState, toNeighbor.getOpposite())) {
            return stateFromConnector(state, neighborState, connector, toNeighbor);
        } else if (getInputFace(state) == toNeighbor && (state.get(MECHANICAL_SOURCE) > 0 || state.get(BEARING_LOAD))) {
            return state.with(MECHANICAL_SOURCE, 0).with(BEARING_LOAD, false);
        } else {
            return state;
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction incomingFace, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (inputOrOutput(state, incomingFace)) {
            return getMechanicalUpdate(state, neighborState, incomingFace);
        }

        return state;
    }

    static {
        Double[] coordsB1 = {6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D};

        SHAPE = createCuboidShapeFromArray(coordsB1);
        SHAPE_X = createCuboidShapeFromArray(rotateCuboidCoords(coordsB1, 1));
        SHAPE_Z = createCuboidShapeFromArray(rotateCuboidCoords(coordsB1, 2));
    }

    // This assumes the source outputs in axis of axle
    public static BlockState stateFromSource(BlockState state, BlockState sourceState, MechanicalSource source, Direction toSource) {
        int sourceSource = source.getSourceAtFace(sourceState, toSource.getOpposite());
        int selfSource = state.get(MECHANICAL_SOURCE);
        boolean sourceBearing = source.getBearingAtFace(sourceState, toSource.getOpposite());
        boolean selfBearing = state.get(BEARING_LOAD);

        BlockState newState = state;
        // If this source nonzero, switch direction
        // If this then later hits a source, source will cause breakage
        if (getInputFace(state) != toSource && sourceSource > 0) {
            newState = switchInputOutput(newState);
        }

        if (sourceSource == selfSource && sourceBearing == selfBearing) {
            return state;
        }

        if (sourceSource != selfSource) {
            newState = newState.with(MECHANICAL_SOURCE, sourceSource);
        }
        if (sourceBearing != selfBearing) {
            newState = newState.with(BEARING_LOAD, sourceBearing);
        }
        return newState;
    }

    // This assumes the connector outputs into axle
    public static BlockState stateFromConnector(BlockState state, BlockState connectorState, MechanicalConnector connector, Direction toConnector) {
        return connectorState;
    }

    // This assumes the source sinks in axis of axle
    public static BlockState stateFromSink(BlockState state, BlockState sinkState, MechanicalSink sink, Direction toSink) {
        int sinkSink = sink.getSink(sinkState);
        int selfSink = state.get(MECHANICAL_SINK);

        BlockState newState = state;
        // If sink zero and this sink nonzero, switch direction
        // Since in this case there cannot be a source
        if (getInputFace(state).getOpposite() != toSink && sinkSink > 0) {
            newState = switchInputOutput(newState);
        }

        // If sink request is not equal, change our sink request
        if (sinkSink != selfSink) {
            boolean selfBearing = state.get(BEARING_LOAD);

            // If requesting more, temporarily it must set bearing load to false and then wait for it to be
            // re-activated
            if (sinkSink > selfSink && selfBearing) {
                newState = newState.with(BEARING_LOAD, false);
            }
            newState = newState.with(MECHANICAL_SINK, sinkSink);
        }
        return newState;
    }

    public void update(World world, BlockPos pos, BlockState state) {
        BlockState front = world.getBlockState(pos.offset(state.get(PillarBlock.AXIS), 1));
        BlockState back = world.getBlockState(pos.offset(state.get(PillarBlock.AXIS), -1));


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
