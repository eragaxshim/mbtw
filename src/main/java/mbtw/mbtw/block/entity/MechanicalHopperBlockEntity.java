package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.MechanicalSink;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MechanicalHopperBlockEntity extends HopperBlockEntity implements MechanicalSinkBlockEntity {
    private int sink;

    public MechanicalHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public MechanicalSink sinkBlock() {
        return (MechanicalSink) Mbtw.MECHANICAL_HOPPER;
    }

    @Override
    public int getAvailablePower() {
        return 0;
    }

    @Override
    public int getSink() {
        return 0;
    }

    @Override
    public void setAvailablePower(int availablePower) {

    }

    @Override
    public void setSink(int sink) {

    }


}
