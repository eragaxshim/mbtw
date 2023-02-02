package mbtw.mbtw.block;

import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

public class AxleBlock extends Block implements MechanicalTransmission {
    DirectionProperty INPUT_FACE = Properties.FACING;
    public AxleBlock(Settings settings) {
        super(settings);
    }
}
