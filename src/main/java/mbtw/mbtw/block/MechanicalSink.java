package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface MechanicalSink {
    int getMaxSink(BlockState state);

    // Request for mechanical power
    int getSink(BlockState state);

    boolean isPowered(BlockState state);

    default int getAvailablePower(BlockState state) {
        return isPowered(state) ? getSink(state) : 0;
    }

    BlockState setAvailablePower(BlockState state, int availablePower);
    BlockState setSink(BlockState state, int sink);

    // Accepts mechanical power at this face
    boolean isSinkAtFace(BlockState state, Direction sinkFace);

    // All faces that could accept mechanical power
    List<Direction> getInputFaces(BlockState state);

    // Whether multiple input faces allow different mechanical power
    // If not, it takes the minimum, if yes, it takes the maximum of the two
    boolean incongruentInputAllowed(BlockState state);
}
