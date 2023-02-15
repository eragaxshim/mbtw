package mbtw.mbtw.block;

import mbtw.mbtw.block.entity.MechanicalSinkBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MechanicalHopper extends HopperBlock implements MechanicalSink {
    private final List<Direction> INPUT_FACES = List.of(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH);

    public MechanicalHopper(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxSink(BlockState state) {
        return 4;
    }

    @Override
    public int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return MechanicalSinkBlockEntity.blockGetSink(world, pos, blockEntity);
    }

    @Override
    public boolean isPowered(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return false;
    }

    @Override
    public boolean isSinkAtFace(BlockState state, Direction sinkFace) {
        // Horizontal
        return sinkFace.getVector().getY() == 0;
    }

    @Override
    public List<Direction> getInputFaces(BlockState state) {
        return INPUT_FACES;
    }

    @Override
    public boolean incongruentInputAllowed(BlockState state) {
        return false;
    }
}
