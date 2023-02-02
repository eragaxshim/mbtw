package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class InfiniteCrankBlockEntity extends BlockEntity {

    public InfiniteCrankBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.INFINITE_CRANK_ENTITY, pos, state);
    }
}
