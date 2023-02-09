package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.List;

public class InfiniteCrankBlock extends Block implements MechanicalSource {

    public InfiniteCrankBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState());
    }

    @Override
    public boolean isSourceAtFace(BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public int getSourceAtFace(BlockState state, Direction face) {
        return face == Direction.DOWN ? 2 : 0;
    }

    @Override
    public boolean getBearingAtFace(BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public List<Direction> getOutputFaces(BlockState sourceState) {
        return List.of(Direction.DOWN);
    }
}
