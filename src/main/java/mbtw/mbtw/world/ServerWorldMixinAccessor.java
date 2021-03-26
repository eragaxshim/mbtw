package mbtw.mbtw.world;

public interface ServerWorldMixinAccessor {
    BlockScheduleManager getChunkedScheduleManager();

    ItemTickManager1 getItemTickManager();
}
