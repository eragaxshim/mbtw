package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class MillstoneBlockEntity extends BlockEntity {
    public MillstoneBlockEntity( BlockPos pos, BlockState state) {
        super(Mbtw.MILLSTONE_ENTITY, pos, state);
    }
}
