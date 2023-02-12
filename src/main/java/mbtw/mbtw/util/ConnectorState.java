package mbtw.mbtw.util;

import mbtw.mbtw.block.MechanicalConnector;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record ConnectorState(MechanicalConnector connector, BlockPos pos, BlockState blockState, Direction direction, int source, int sink, boolean bearing, boolean atMaxSink, int ratio) {
    public ConnectorState(MechanicalConnector connector, BlockPos pos, BlockState blockState, Direction direction, int source, int sink, boolean bearing, int ratio) {
        this(connector, pos, blockState, direction, source, sink, bearing, sink == source, ratio);
    }

    public ConnectorState(MechanicalConnector connector, BlockPos pos, BlockState blockState, Direction direction, int source, int sink, boolean bearing) {
        this(connector, pos, blockState, direction, source, sink, bearing, sink == source, 1);
    }
}
