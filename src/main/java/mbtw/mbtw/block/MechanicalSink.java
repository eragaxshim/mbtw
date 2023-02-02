package mbtw.mbtw.block;

import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public interface MechanicalSink extends MechanicalTransmission {
    boolean addSource(World world, MechanicalVec rotVec, BlockPos sourcePos, BlockState sourceState, BlockPos pos, BlockState state);

}
