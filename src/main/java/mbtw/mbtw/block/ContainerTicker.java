package mbtw.mbtw.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerTicker {
    public static <T extends BlockEntity> void emptyTick(World world, BlockPos pos, BlockState state, T blockEntity) {

    }
}
