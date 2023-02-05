package mbtw.mbtw.block;

import net.minecraft.block.BlockState;

/**
 * Mechanical Power is a simple system.
 */
public interface MechanicalConnector {
    int getSource(BlockState connectorState);

    boolean getBearing(BlockState connectorState);

    int getSink(BlockState connectorState);

}
