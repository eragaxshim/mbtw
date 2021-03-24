package mbtw.mbtw.world;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class BlockScheduleManager extends PersistentState {
    private final ServerWorld world;
    private HashMap<ChunkPos, ChunkSchedule> scheduledChunks = new HashMap<>();
    private final HashSet<ChunkPos> removedChunks = new HashSet<>();

    public BlockScheduleManager(ServerWorld world) {
        super(nameFor(world.getDimension()));
        this.world = world;
    }

    public void schedule(int time, BlockSchedule blockSchedule)
    {
        ChunkPos chunkPos = new ChunkPos(blockSchedule.getBlockPos());
        ChunkSchedule chunkToSchedule = this.scheduledChunks.getOrDefault(chunkPos, null);
        if (chunkToSchedule != null)
        {
            chunkToSchedule.schedule(time, blockSchedule);
        }
        else {
            chunkToSchedule = new ChunkSchedule(time, blockSchedule);
            this.scheduledChunks.put(chunkPos, chunkToSchedule);
        }
    }

    public void tick(ChunkPos chunkPos)
    {
        if (this.world.getTime() % 20 == 0)
        {
            ChunkSchedule scheduleToTick = this.scheduledChunks.getOrDefault(chunkPos, null);
            if (scheduleToTick != null)
            {
                scheduleToTick.tick(this.world, chunkPos, this);
            }
        }

        if (!this.removedChunks.isEmpty())
        {
            for (ChunkPos chunkPosToRemove : this.removedChunks)
            {
                this.scheduledChunks.remove(chunkPosToRemove);
            }
            this.removedChunks.clear();
        }
    }

    public void setRemove(ChunkPos chunkPos)
    {
        this.removedChunks.add(chunkPos);
    }

    public void onBlockChanged(BlockPos pos)
    {
        if (!scheduledChunks.isEmpty())
        {
            ChunkPos chunkPos = new ChunkPos(pos);
            ChunkSchedule scheduleToChange = this.scheduledChunks.getOrDefault(chunkPos, null);
            if (scheduleToChange != null)
            {
                scheduleToChange.onBlockChanged(pos);
            }
        }

    }

    public static String nameFor(DimensionType dimensionType) {
        return "scheduler" + dimensionType.getSuffix();
    }

    public void fromTag(CompoundTag tag) {
        ListTag listTag = tag.getList("ScheduledChunks", 10);

        HashMap<ChunkPos, ChunkSchedule> tagScheduledChunks = new HashMap<>();
        for (int i = 0; i < listTag.size(); ++i)
        {
            CompoundTag scheduledChunkEntry = listTag.getCompound(i);

            int chunkX = scheduledChunkEntry.getInt("X");
            int chunkY = scheduledChunkEntry.getInt("Z");
            ChunkPos chunkPos = new ChunkPos(chunkX, chunkY);
            ChunkSchedule chunkSchedule = ChunkSchedule.scheduleFromTag(scheduledChunkEntry.getCompound("ChunkSchedule"));
            tagScheduledChunks.put(chunkPos, chunkSchedule);
        }
        this.scheduledChunks = tagScheduledChunks;
    }

    public CompoundTag toTag(CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (Map.Entry<ChunkPos, ChunkSchedule> entry : scheduledChunks.entrySet())
        {
            CompoundTag scheduledChunkEntry = new CompoundTag();
            scheduledChunkEntry.putInt("X", entry.getKey().x);
            scheduledChunkEntry.putInt("Z", entry.getKey().z);

            scheduledChunkEntry.put("ChunkSchedule", entry.getValue().createTag());
            listTag.add(scheduledChunkEntry);
        }
        tag.put("ScheduledChunks", listTag);
        return tag;
    }
}
