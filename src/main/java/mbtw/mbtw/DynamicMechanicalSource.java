package mbtw.mbtw;

import mbtw.mbtw.block.MechanicalSource;
import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import mbtw.mbtw.block.entity.MechanicalSourceBlockEntity;
import mbtw.mbtw.util.math.DirectionHelper;
import mbtw.mbtw.util.math.Relative;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DynamicMechanicalSource extends MechanicalSource {
    int getAvailableDelivery(World world, BlockState state, BlockPos pos, @Nullable MechanicalSourceBlockEntity blockEntity);

    int computeSourceAtFace(BlockState state, Direction face, int sourceBase);

    int costPerBase(BlockState state, List<Direction> includedFaces);

    int getRatioAtFace(BlockState state, Direction face);
}
