package mbtw.mbtw.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

public interface ChunkedTickable<T extends ChunkedTickable<T>> {
    void tick(ServerWorld world, ChunkPos chunkPos, ChunkedPersistentState<T> chunkedPersistentState);

    T buildFromTag(NbtCompound tag);

    NbtCompound createTag();
}
