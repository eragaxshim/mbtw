package mbtw.mbtw.block;

import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface MechanicalSink {
    int getMaxSink();

    int getSink(BlockState state);

    boolean isSinkAtFace(BlockState state, Direction sinkFace);
}
