package mbtw.mbtw.block;

import mbtw.mbtw.DynamicMechanicalSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class InfiniteCrankBlock extends Block implements MechanicalSource {

    public static List<Direction> DIRECTIONS = List.of(Direction.values());

    public InfiniteCrankBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState());
    }

    @Override
    public boolean isSourceAtFace(BlockState state, Direction face) {
        return true;
    }

    @Override
    public int getSourceAtFace(BlockState state, Direction face) {
        return 2;
    }

    @Override
    public boolean getBearingAtFace(BlockState state, Direction face) {
        return true;
    }

    @Override
    public List<Direction> getOutputFaces(BlockState sourceState) {
        return DIRECTIONS;
    }
}
