package mbtw.mbtw.block;

import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface MechanicalSink {
    boolean addSource(World world, MechanicalVec rotVec, BlockPos sourcePos, BlockState sourceState, BlockPos pos, BlockState state);

    int getMaxSink();

    int getSink(BlockState state);
}
