package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MechanicalSink {
    int getMaxSink(BlockState state);

    // Request for mechanical power
    int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity);

    boolean isPowered(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity);

    default int getAvailablePower(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return isPowered(world, state, pos, blockEntity) ? getSink(world, state, pos, blockEntity) : 0;
    }

    // Accepts mechanical power at this face
    boolean isSinkAtFace(BlockState state, Direction sinkFace);

    // All faces that could accept mechanical power
    List<Direction> getInputFaces(BlockState state);

    // Whether multiple input faces allow different mechanical power
    // If not, it takes the minimum, if yes, it takes the maximum of the two
    boolean incongruentInputAllowed(BlockState state);
}
