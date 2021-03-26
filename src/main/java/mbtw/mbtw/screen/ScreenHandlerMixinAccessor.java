package mbtw.mbtw.screen;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface ScreenHandlerMixinAccessor {

    void tickTrackedStacks(World world, Entity entity);
}
