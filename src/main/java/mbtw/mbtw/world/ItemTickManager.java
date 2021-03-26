package mbtw.mbtw.world;

import net.minecraft.server.world.ServerWorld;

public class ItemTickManager extends ChunkedPersistentState<ChunkItems> {
    public static final String key = "itemTicker";

    public ItemTickManager(ServerWorld world) {
        super(nameFor(world.getDimension(), ItemTickManager.key), world, new ChunkItems(), "ChunkItems");
    }
}
