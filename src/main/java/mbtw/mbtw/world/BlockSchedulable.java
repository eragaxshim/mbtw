package mbtw.mbtw.world;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface BlockSchedulable {
    void runScheduled(ServerWorld world, BlockState state, BlockPos pos);
}
