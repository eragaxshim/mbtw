package mbtw.mbtw.world;

public interface ServerWorldMixinAccessor {
    BlockScheduleManager getChunkedScheduleManager();

    ItemTickManager getItemTickManager();
}
