package mbtw.mbtw.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

/**
 * Mechanical Power is a simple system.
 */
public interface MechanicalConnector {
    BlockState withSink(BlockState connectorState, int newSink);

    int getSource(BlockState connectorState);

    boolean getBearing(BlockState connectorState);

    int getSink(BlockState connectorState);

    boolean isOutputAtFace(BlockState connectorState, Direction outputFace);

}
