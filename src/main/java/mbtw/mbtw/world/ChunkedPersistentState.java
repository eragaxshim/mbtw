package mbtw.mbtw.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class ChunkedPersistentState<T extends ChunkedTickable<T>> extends PersistentState {
    private final ServerWorld world;
    public Object2ObjectOpenHashMap<ChunkPos, T> tickableChunks = new Object2ObjectOpenHashMap<>();
    private final ObjectOpenHashSet<ChunkPos> removedChunks = new ObjectOpenHashSet<>();
    private final String chunkedTickableKey;
    private final T tickableUnit;

    public ChunkedPersistentState(String key, ServerWorld world, T tickableUnit, String chunkedTickableKey) {
        this.tickableUnit = tickableUnit;
        this.chunkedTickableKey = chunkedTickableKey;
        this.world = world;
    }

    public void tick(ChunkPos chunkPos)
    {
        if (this.world.getTime() % 20 == 0)
        {
            T chunkedTickable = this.tickableChunks.getOrDefault(chunkPos, null);
            if (chunkedTickable != null)
            {
                chunkedTickable.tick(this.world, chunkPos, this);
            }
        }

        if (!this.removedChunks.isEmpty())
        {
            for (ChunkPos chunkPosToRemove : this.removedChunks)
            {
                this.tickableChunks.remove(chunkPosToRemove);
            }
            this.removedChunks.clear();
        }
    }

    public void setRemove(ChunkPos chunkPos)
    {
        this.removedChunks.add(chunkPos);
    }

    public static String nameFor(DimensionType dimensionType, String key) {
        return key + dimensionType.getSuffix();
    }

    public void persistFromNbt(NbtCompound tag) {
        NbtList listTag = tag.getList("TickableChunks", 10);

        Object2ObjectOpenHashMap<ChunkPos, T> tagTickableChunks = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < listTag.size(); ++i)
        {
            NbtCompound scheduledChunkEntry = listTag.getCompound(i);

            int chunkX = scheduledChunkEntry.getInt("X");
            int chunkY = scheduledChunkEntry.getInt("Z");
            ChunkPos chunkPos = new ChunkPos(chunkX, chunkY);
            tagTickableChunks.put(chunkPos, this.tickableUnit.buildFromTag(scheduledChunkEntry.getCompound(this.chunkedTickableKey)));
        }
        this.tickableChunks = tagTickableChunks;
    }

    public NbtCompound writeNbt(NbtCompound tag) {
        NbtList listTag = new NbtList();
        for (Map.Entry<ChunkPos, T> entry : this.tickableChunks.entrySet())
        {
            NbtCompound scheduledChunkEntry = new NbtCompound();
            scheduledChunkEntry.putInt("X", entry.getKey().x);
            scheduledChunkEntry.putInt("Z", entry.getKey().z);

            scheduledChunkEntry.put(this.chunkedTickableKey, entry.getValue().createTag());
            listTag.add(scheduledChunkEntry);
        }
        tag.put("TickableChunks", listTag);
        return tag;
    }
}
