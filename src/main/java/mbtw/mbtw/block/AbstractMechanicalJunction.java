package mbtw.mbtw.block;

import net.minecraft.block.Block;

public abstract class AbstractMechanicalJunction extends Block implements MechanicalSource, MechanicalSink {
    public AbstractMechanicalJunction(Settings settings) {
        super(settings);
    }
}
