package mbtw.mbtw.world;

public interface BlockSchedulable {
    default void runScheduled() {
        System.out.println("ranScheduled!");
    }
}
