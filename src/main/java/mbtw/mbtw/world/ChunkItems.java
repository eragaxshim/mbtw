package mbtw.mbtw.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

public class ChunkItems implements ChunkedTickable<ChunkItems> {
    private Long2ObjectOpenHashMap<ItemStack> tickingStacks = new Long2ObjectOpenHashMap<>();

    public ChunkItems()
    {

    }


    public void tick(ServerWorld world, ChunkPos chunkPos, ChunkedPersistentState<ChunkItems> chunkedPersistentState) {

    }


    public ChunkItems buildFromTag(CompoundTag tag) {
        return null;
    }


    public CompoundTag createTag() {
        return null;
    }
}
