package mbtw.mbtw.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;

public class AxleBlock extends PillarBlock implements MechanicalConnector {
    public static final DirectionProperty INPUT_FACE = Properties.FACING;
    public AxleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return changeRotation(state, rotation).with(INPUT_FACE, rotation.rotate(state.get(INPUT_FACE)));
    }

    public static BlockState switchInputOutput(BlockState state) {
        return state.with(INPUT_FACE, state.get(INPUT_FACE).getOpposite());
    }
}
