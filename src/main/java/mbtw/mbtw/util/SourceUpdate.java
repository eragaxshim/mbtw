package mbtw.mbtw.util;

import mbtw.mbtw.DynamicMechanicalSource;
import mbtw.mbtw.block.MechanicalConnector;
import mbtw.mbtw.block.entity.MechanicalSourceBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SourceUpdate {
    private final World world;
    private final DynamicMechanicalSource source;
    private final MechanicalSourceBlockEntity sourceEntity;
    private final BlockPos sourcePos;
    private BlockState sourceState;
    // We sort based on ratio because we want the max ratio
    private final SortedSet<ConnectorState> connectors = new TreeSet<>(Comparator.comparing(ConnectorState::ratio));
    private static final ConnectorState EMPTY = new ConnectorState(null, null, null, null, 0, 0, false);
    private int maxConnectedRatio;
    private final List<Direction> sinkingFaces;

    public SourceUpdate(World world, BlockPos sourcePos, BlockState sourceState, MechanicalSourceBlockEntity sourceEntity) {
        this.world = world;
        this.source = sourceEntity.source();
        this.sourceEntity = sourceEntity;
        this.sourcePos = sourcePos;
        this.sourceState = sourceState;
        List<Direction> sinkingFaces = new ArrayList<>();

        for (Direction direction : source.getOutputFaces(sourceState)) {
            BlockPos neighborPos = sourcePos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            // Only add with nonzero sink
            if (neighborState.getBlock() instanceof MechanicalConnector connector && connector.isOutputAtFace(neighborState, direction)) {
                connectors.add(new ConnectorState(connector, neighborPos, neighborState, direction, connector.getSource(neighborState), connector.getSink(neighborState), connector.getBearing(neighborState), source.getRatioAtFace(sourceState, direction)));
                if (connector.getSink(neighborState) > 0) {
                    sinkingFaces.add(direction);
                }
            }
        }

        this.maxConnectedRatio = computeMaxConnectedRatio();

        this.sinkingFaces = sinkingFaces;
    }

    public int computeMaxConnectedRatio() {
        // Default int sort is ascending order
        if (!connectors.isEmpty()) {
            return connectors.last().ratio();
        } else {
            return 1;
        }
    }

    public @Nullable ConnectorState findBreak() {
        if (!connectors.isEmpty()) {
            return connectors.last();
        } else {
            return null;
        }
    }

    public void reloadAfterRemove(ConnectorState csToRemove) {
        sourceState = world.getBlockState(sourcePos);
        connectors.remove(csToRemove);
        maxConnectedRatio = computeMaxConnectedRatio();
        sinkingFaces.clear();
        for (ConnectorState connector : connectors) {
            if (connector.connector().getSink(connector.blockState()) > 0) {
                sinkingFaces.add(connector.direction());
            }
        }
    }

    public int updateSourceBase() {
        int availableDelivery = source.getAvailableDelivery(world, sourceState, sourcePos, sourceEntity);
        if (availableDelivery == 0) {
            sourceEntity.worldSetSourceBase(world, sourcePos, sourceState, 0);
            return 0;
        }

        int costPerBase = Math.max(source.costPerBase(sourceState, sinkingFaces), maxConnectedRatio);
        int newBase = availableDelivery / costPerBase;

        if (newBase != 0) {
            sourceEntity.worldSetSourceBase(world, sourcePos, sourceState, newBase);
            return newBase;
        }

        // We assume available delivery does not change
        ConnectorState connectorToBreak;
        while ((connectorToBreak = findBreak()) != null && newBase == 0) {
            sourceEntity.worldBreakConnector(world, connectorToBreak.pos());
            reloadAfterRemove(connectorToBreak);
            costPerBase = Math.max(source.costPerBase(sourceState, sinkingFaces), maxConnectedRatio);
            newBase = availableDelivery / costPerBase;
        }

        sourceEntity.worldSetSourceBase(world, sourcePos, sourceState, newBase);
        return newBase;
    }

    public void updateBearing(int newBase) {
        if (newBase == 0) {
            sourceEntity.worldSetBearing(world, sourcePos, sourceState, List.of());
        } else {
            List<Direction> bearingDirections = connectors.stream()
                    .filter(cs ->  source.computeSourceAtFace(sourceState, cs.direction(), newBase) == cs.sink())
                    .map(ConnectorState::direction).toList();
            sourceEntity.worldSetBearing(world, sourcePos, sourceState, bearingDirections);
        }
    }
}
