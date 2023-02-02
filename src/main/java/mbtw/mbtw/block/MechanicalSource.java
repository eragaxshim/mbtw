package mbtw.mbtw.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public interface MechanicalSource extends MechanicalTransmission {

    int getOutRotation(World world, BlockPos pos, BlockState state, Vec3i sourceToSink);
}
