package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.MechanicalSourceBlockEntity;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface MechanicalSource {

    boolean isSourceAtFace(BlockState state, Direction face);

    // Guaranteed to be up to date
    int getSourceAtFace(BlockState state, Direction face);

    boolean getBearingAtFace(World world, BlockState state, BlockPos pos, @Nullable MechanicalSourceBlockEntity blockEntity, Direction face);

    List<Direction> getOutputFaces(BlockState sourceState);
}
