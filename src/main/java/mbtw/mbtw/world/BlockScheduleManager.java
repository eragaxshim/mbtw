package mbtw.mbtw.world;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BlockScheduleManager extends ChunkedPersistentState<ChunkSchedule> {
    public static final String key = "scheduler";

    public BlockScheduleManager(ServerWorld world) {
        super(nameFor(world.getDimension(), BlockScheduleManager.key), world, new ChunkSchedule(), "ChunkSchedule");
    }

    public void schedule(int time, BlockSchedule blockSchedule)
    {
        ChunkPos chunkPos = new ChunkPos(blockSchedule.getBlockPos());
        ChunkSchedule chunkToSchedule = this.tickableChunks.getOrDefault(chunkPos, null);
        if (chunkToSchedule != null)
        {
            chunkToSchedule.schedule(time, blockSchedule);
        }
        else {
            chunkToSchedule = new ChunkSchedule(time, blockSchedule);
            this.tickableChunks.put(chunkPos, chunkToSchedule);
        }
    }

    public void onBlockChanged(BlockPos pos, BlockState newState)
    {
        if (!this.tickableChunks.isEmpty())
        {
            ChunkPos chunkPos = new ChunkPos(pos);
            ChunkSchedule scheduleToChange = this.tickableChunks.getOrDefault(chunkPos, null);
            if (scheduleToChange != null)
            {
                scheduleToChange.onBlockChanged(pos, newState);
            }
        }

    }
}
