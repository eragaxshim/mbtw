package mbtw.mbtw.util;

import mbtw.mbtw.block.MechanicalConnector;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SinkUpdate {
    private final World world;
    private final MechanicalSink sink;
    private final MechanicalSinkBlockEntity sinkEntity;
    private final BlockPos sinkPos;
    private final BlockState sinkState;
    private final List<ConnectorState> connectors = new ArrayList<>();
    private static final ConnectorState EMPTY = new ConnectorState(null, null, null, null, 0, 0, false);
    private int maxSource = -1;
    private int availablePower = -1;

    public SinkUpdate(World world, BlockPos sinkPos, BlockState sinkState, MechanicalSinkBlockEntity sinkEntity) {
        this.world = world;
        this.sink = sinkEntity.sinkBlock();
        this.sinkEntity = sinkEntity;
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
            availablePower = connectors.stream().max(Comparator.comparing(sinkState -> sinkState.bearing() ? sinkState.source() : 0)).orElse(EMPTY).source();
        } else {
            // Source of the connector with bearing load, positive source with the least source
            availablePower = connectors.stream().filter(sinkState -> sinkState.bearing() && sinkState.source() > 0).min(Comparator.comparing(ConnectorState::source)).orElse(EMPTY).source();
        }

        return availablePower;
    }

    public int maxSource() {
        if (maxSource != -1) {
            return maxSource;
        }
        if (sink.incongruentInputAllowed(sinkState)) {
            maxSource = connectors.stream().max(Comparator.comparing(ConnectorState::source)).orElse(EMPTY).source();
        } else {
            // Source of the connector with positive source with the least source
            maxSource = connectors.stream().filter(sinkState -> sinkState.source() > 0).min(Comparator.comparing(ConnectorState::source)).orElse(EMPTY).source();
        }

        return maxSource;
    }

    public boolean updateSinkPower() {
        if (availablePower() != sink.getAvailablePower(world, sinkState, sinkPos, sinkEntity)) {
            sinkEntity.worldSetAvailablePower(world,  sinkState, sinkPos, availablePower());
            return true;
        }
        return false;
    }

    public boolean updateSink() {
        if (sink.getSink(world, sinkState, sinkPos, sinkEntity) != maxSource()) {
            sinkEntity.worldSetSink(world, sinkState, sinkPos, maxSource());
            return true;
        }
        return false;
    }
}


