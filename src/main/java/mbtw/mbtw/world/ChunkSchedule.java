package mbtw.mbtw.world;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.stream.Collectors;

public class ChunkSchedule implements ChunkedTickable<ChunkSchedule> {
    private TreeMap<Long, HashSet<BlockSchedule>> scheduledBlocks = new TreeMap<>();
    private HashMap<BlockPos, List<Long>> posTimesMap = new HashMap<>();
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

    public ChunkSchedule(long chunkTime, TreeMap<Long, HashSet<BlockSchedule>> scheduledBlocks, HashMap<BlockPos, List<Long>> posTimesMap)
    {
        this.chunkTime = chunkTime;
        this.scheduledBlocks = scheduledBlocks;
        this.posTimesMap = posTimesMap;
    }

    public void schedule(long time, BlockSchedule blockSchedule)
    {
        long newTime = this.chunkTime + time;
        HashSet<BlockSchedule> timeToSchedule = scheduledBlocks.getOrDefault(newTime, new HashSet<>());
        if (timeToSchedule.isEmpty())
        {
            this.scheduledBlocks.put(newTime, timeToSchedule);
        }
        List<Long> posTimes = this.posTimesMap.getOrDefault(blockSchedule.getBlockPos(), new ArrayList<>());
        this.posTimesMap.put(blockSchedule.getBlockPos(), posTimes);
    }

    public void tick(ServerWorld world, ChunkPos chunkPos, ChunkedPersistentState<ChunkSchedule> chunkedPersistentState) {
        if (!this.scheduledBlocks.isEmpty() && this.chunkTime == this.scheduledBlocks.firstKey())
        {
            HashSet<BlockSchedule> blockSchedules = this.scheduledBlocks.firstEntry().getValue();
            for (BlockSchedule blockSchedule : blockSchedules)
            {
                blockSchedule.runSchedule(world);
                List<Long> posTimes = this.posTimesMap.get(blockSchedule.getBlockPos());
                posTimes.remove(this.chunkTime);
                if (posTimes.isEmpty())
                {
                    this.posTimesMap.remove(blockSchedule.getBlockPos());
                }
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

    public ChunkSchedule buildFromTag(NbtCompound tag) {
        long tagChunkTime = tag.getLong("Tick");
        NbtList listTag = tag.getList("ScheduledBlocks", 10);

        TreeMap<Long, HashSet<BlockSchedule>> tagScheduledBlocks = new TreeMap<>();
        HashMap<BlockPos, List<Long>> tagPosTimesMap = new HashMap<>();
        for (int i = 0; i < listTag.size(); ++i)
        {
            NbtCompound tickSchedulesEntry = listTag.getCompound(i);
            long scheduleTime = tickSchedulesEntry.getLong("ScheduleTime");
            NbtList tickSchedules = tickSchedulesEntry.getList("TickSchedules", 10);
            HashSet<BlockSchedule> tickSchedule = new HashSet<>();
            for (int j = 0; j < tickSchedules.size(); j++)
            {
                BlockSchedule blockSchedule = BlockSchedule.scheduleFromTag(tickSchedules.getCompound(j));
                List<Long> posTimes = tagPosTimesMap.getOrDefault(blockSchedule.getBlockPos(), new ArrayList<>());
                tagPosTimesMap.put(blockSchedule.getBlockPos(), posTimes);
                tickSchedule.add(blockSchedule);
            }
            scheduledBlocks.put(scheduleTime, tickSchedule);
        }
        return new ChunkSchedule(tagChunkTime, tagScheduledBlocks, tagPosTimesMap);
    }


    public NbtCompound createTag() {
        NbtCompound tag = new NbtCompound();
        tag.putLong("Tick", this.chunkTime);
        NbtList listTag = new NbtList();
        for (Map.Entry<Long, HashSet<BlockSchedule>> entry : scheduledBlocks.entrySet())
        {
            NbtCompound tickSchedulesEntry = new NbtCompound();
            tickSchedulesEntry.putLong("ScheduleTime", entry.getKey());
            NbtList tickSchedules = new NbtList();
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

    public void onBlockChanged(BlockPos pos, BlockState newState)
    {
        List<Long> posTimes = this.posTimesMap.getOrDefault(pos, null);
        if (posTimes != null)
        {
            List<Long> removedLongs = new ArrayList<>();
            for (long posTime : posTimes)
            {
                HashSet<BlockSchedule> tickSchedules = this.scheduledBlocks.get(posTime);
                List<BlockSchedule> removedSchedules = tickSchedules.stream()
                        .filter(blockSchedule -> blockSchedule.getBlockPos() == pos && !blockSchedule.compliesToProperties(newState))
                        .collect(Collectors.toList());
                tickSchedules.removeAll(removedSchedules);
                if (tickSchedules.isEmpty())
                {
                    removedLongs.add(posTime);
                    this.scheduledBlocks.remove(posTime);
                }
            }
            posTimes.removeAll(removedLongs);
            if (posTimes.isEmpty())
            {
                this.posTimesMap.remove(pos);
            }
        }
    }
}
