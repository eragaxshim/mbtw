package mbtw.mbtw.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ChunkSchedule implements ChunkedTickable<ChunkSchedule> {
    private TreeMap<Long, HashSet<BlockSchedule>> scheduledBlocks = new TreeMap<>();
    private final TreeMap<BlockPos, Long> posTimeMap = new TreeMap<>();
    private long chunkTime;

    public ChunkSchedule()
    {
        this.chunkTime = 0;
    }

    public ChunkSchedule(int time, BlockSchedule blockSchedule)
    {
        this.chunkTime = 0;
        schedule(time, blockSchedule);
    }

    public ChunkSchedule(long chunkTime, TreeMap<Long, HashSet<BlockSchedule>> scheduledBlocks)
    {
        this.chunkTime = chunkTime;
        this.scheduledBlocks = scheduledBlocks;
    }

    public void schedule(long time, BlockSchedule blockSchedule)
    {
        long newTime = this.chunkTime + time;
        HashSet<BlockSchedule> timeToSchedule = scheduledBlocks.getOrDefault(newTime, new HashSet<>());
        if (timeToSchedule.isEmpty())
        {
            this.scheduledBlocks.put(newTime, timeToSchedule);
        }
        timeToSchedule.add(blockSchedule);
        this.posTimeMap.put(blockSchedule.getBlockPos(), newTime);
    }

    public void tick(ServerWorld world, ChunkPos chunkPos, ChunkedPersistentState<ChunkSchedule> chunkedPersistentState) {
        if (!this.scheduledBlocks.isEmpty() && this.chunkTime == this.scheduledBlocks.firstKey())
        {
            HashSet<BlockSchedule> blockSchedules = this.scheduledBlocks.firstEntry().getValue();
            for (BlockSchedule blockSchedule : blockSchedules)
            {
                blockSchedule.runSchedule(world);
                posTimeMap.remove(blockSchedule.getBlockPos());
            }
            this.scheduledBlocks.remove(this.scheduledBlocks.firstKey());
            if (this.scheduledBlocks.isEmpty())
            {
                chunkedPersistentState.setRemove(chunkPos);
            }
        }
        this.chunkTime++;
        chunkedPersistentState.markDirty();
    }

    public ChunkSchedule buildFromTag(CompoundTag tag) {
        long chunkTime = tag.getLong("Tick");
        ListTag listTag = tag.getList("ScheduledBlocks", 10);

        TreeMap<Long, HashSet<BlockSchedule>> scheduledBlocks = new TreeMap<>();
        for (int i = 0; i < listTag.size(); ++i)
        {
            CompoundTag tickSchedulesEntry = listTag.getCompound(i);
            long scheduleTime = tickSchedulesEntry.getLong("ScheduleTime");
            ListTag tickSchedules = tickSchedulesEntry.getList("TickSchedules", 10);
            HashSet<BlockSchedule> tickSchedule = new HashSet<>();
            for (int j = 0; j < tickSchedules.size(); j++)
            {
                tickSchedule.add(BlockSchedule.scheduleFromTag(tickSchedules.getCompound(j)));
            }
            scheduledBlocks.put(scheduleTime, tickSchedule);
        }
        return new ChunkSchedule(chunkTime, scheduledBlocks);
    }


    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Tick", this.chunkTime);
        ListTag listTag = new ListTag();
        for (Map.Entry<Long, HashSet<BlockSchedule>> entry : scheduledBlocks.entrySet())
        {
            CompoundTag tickSchedulesEntry = new CompoundTag();
            tickSchedulesEntry.putLong("ScheduleTime", entry.getKey());
            ListTag tickSchedules = new ListTag();
            for (BlockSchedule blockSchedule : entry.getValue())
            {
                tickSchedules.add(blockSchedule.createTag());
            }
            tickSchedulesEntry.put("TickSchedules", tickSchedules);
            listTag.add(tickSchedulesEntry);
        }
        tag.put("ScheduledBlocks", listTag);
        return tag;
    }

    public void onBlockChanged(BlockPos pos)
    {
        long posTime = posTimeMap.getOrDefault(pos, -1L);
        if (posTime != -1L)
        {
            HashSet<BlockSchedule> tickSchedule = scheduledBlocks.get(posTime);
            Optional<BlockSchedule> o = tickSchedule.stream().
                    filter(bs -> bs.getBlockPos() == pos)
                    .findAny();
            o.ifPresent(tickSchedule::remove);
        }
    }
}
