package mbtw.mbtw.util;

import mbtw.mbtw.DynamicMechanicalSource;
import mbtw.mbtw.block.MechanicalConnector;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SourceUpdate {
    private final World world;
    private final DynamicMechanicalSource source;
    private final BlockPos sourcePos;
    private final BlockState sourceState;
    private final List<ConnectorState> connectors = new ArrayList<>();
    private final List<Direction> directionsToZero = new ArrayList<>();
    private static final ConnectorState EMPTY = new ConnectorState(null, null, null, null, 0, 0, false);
    private final int totalRequested;
    private final int maxConnectedRatio;
    private final List<Direction> sinkingFaces;

    public SourceUpdate(World world, BlockPos sourcePos, BlockState sourceState, DynamicMechanicalSource source) {
        this.world = world;
        this.source = source;
        this.sourcePos = sourcePos;
        this.sourceState = sourceState;
        List<Direction> sinkingFaces = new ArrayList<>();

        for (Direction direction : source.getOutputFaces(sourceState)) {
            BlockPos neighborPos = sourcePos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            // Only add with nonzero sink
            if (neighborState.getBlock() instanceof MechanicalConnector connector && connector.isOutputAtFace(neighborState, direction)) {
                connectors.add(new ConnectorState(connector, neighborPos, neighborState, direction, connector.getSource(neighborState), connector.getSink(neighborState), connector.getBearing(neighborState)));
                if (connector.getSink(neighborState) > 0) {
                    sinkingFaces.add(direction);
                }
            }
        }

        int totalRequested = 0;
        int maxConnectedRatio = 1;
        for (ConnectorState cs : connectors) {
            totalRequested = totalRequested + cs.sink();
            int faceRatio = source.getRatioAtFace(sourceState, cs.direction());
            if (faceRatio > maxConnectedRatio) {
                maxConnectedRatio = faceRatio;
            }
        }
        this.totalRequested = totalRequested;
        this.maxConnectedRatio = maxConnectedRatio;

        this.sinkingFaces = sinkingFaces;
    }

    // This uses saved sourceState, so always call this first
    public BlockState updateSourceBase() {
        int costPerBase = Math.max(source.costPerBase(sourceState, sinkingFaces), maxConnectedRatio);
        int newBase = source.getAvailableDelivery(world, sourceState, sourcePos, null) / costPerBase;
        return source.setSourceBase(sourceState, newBase);
    }

    public BlockState updateBearing(BlockState updatedState) {
        BlockState newState = updatedState;
        if (source.getSourceBase(updatedState) > 0) {
            for (ConnectorState cs : connectors) {
                boolean bearing = source.getSourceAtFace(updatedState, cs.direction()) == cs.sink();
                newState = source.setBearingAtFace(newState, cs.direction(), bearing);
            }
        }
        return newState;
    }
}
