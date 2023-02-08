package mbtw.mbtw.util;

import mbtw.mbtw.block.MechanicalConnector;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SinkUpdate {
    private final World world;
    private final MechanicalSink sink;
    private final BlockPos sinkPos;
    private final BlockState sinkState;
    private final List<ConnectorState> connectors = new ArrayList<>();
    private static final ConnectorState EMPTY = new ConnectorState(null, null, null, null, 0, 0, false);
    private int maxSource = -1;
    private int availablePower = -1;

    public SinkUpdate(World world, BlockPos sinkPos, BlockState sinkState, MechanicalSinkBlockEntity sinkEntity) {
        this.world = world;
        this.sink = sinkEntity.sink();
        this.sinkPos = sinkPos;
        this.sinkState = sinkState;

        for (Direction direction : sink.getInputFaces(sinkState)) {
            BlockPos neighborPos = sinkPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof MechanicalConnector connector && connector.isOutputAtFace(neighborState, direction.getOpposite())) {
                connectors.add(new ConnectorState(connector, neighborPos, neighborState, direction, connector.getSource(neighborState), connector.getSink(neighborState), connector.getBearing(neighborState)));
            }
        }
    }

    public int availablePower() {
        if (availablePower != -1) {
            return availablePower;
        }
        if (sink.incongruentInputAllowed(sinkState)) {
            availablePower = connectors.stream().max(Comparator.comparing(sinkState -> sinkState.bearing ? sinkState.source : 0)).orElse(EMPTY).source;
        } else {
            availablePower = connectors.stream().filter(sinkState -> sinkState.bearing && sinkState.source > 0).min(Comparator.comparing(sinkState -> sinkState.source)).orElse(EMPTY).source;
        }

        return availablePower;
    }

    public int maxSource() {
        if (maxSource != -1) {
            return maxSource;
        }
        if (sink.incongruentInputAllowed(sinkState)) {
            maxSource = connectors.stream().max(Comparator.comparing(sinkState -> sinkState.source)).orElse(EMPTY).source;
        } else {
            maxSource = connectors.stream().filter(sinkState -> sinkState.source > 0).min(Comparator.comparing(sinkState -> sinkState.source)).orElse(EMPTY).source;
        }

        return maxSource;
    }

    public boolean updateSinkPower() {
        if (availablePower() != sink.getAvailablePower(sinkState)) {
            world.setBlockState(sinkPos, sink.setAvailablePower(sinkState, availablePower()));
            return true;
        }
        return false;
    }

    public boolean updateSink() {
        if (sink.getSink(sinkState) != maxSource()) {
            world.setBlockState(sinkPos, sink.setSink(sinkState, maxSource()));
            return true;
        }
        return false;
    }

//    public void updateConnectors() {
//        int maxSink = sink.getMaxSink(sinkState);
//        if (maxSink <= maxSource()) {
//            return;
//        }
//
//        connectors.stream().filter(connectorState -> !connectorState.atMaxSink).forEach(connectorState -> {
//            world.setBlockState(connectorState.pos, connectorState.connector.withSink(connectorState.blockState, Math.min(maxSink, connectorState.source)));
//        });
//    }

    public static class ConnectorState {
        private final MechanicalConnector connector;
        private final BlockPos pos;
        private final BlockState blockState;
        private final Direction direction;
        private final int source;
        private final int sink;
        private final boolean bearing;
        private final boolean atMaxSink;

        public ConnectorState(MechanicalConnector connector, BlockPos pos, BlockState blockState, Direction direction, int source, int sink, boolean bearing) {
            this.connector = connector;
            this.pos = pos;
            this.blockState = blockState;
            this.direction = direction;
            this.source = source;
            this.sink = sink;
            this.bearing = bearing;
            this.atMaxSink = sink == source;
        }
    }
}


