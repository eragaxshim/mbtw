package mbtw.mbtw.block.entity;

import net.minecraft.block.BlockState;

public interface CampfireBlockMixinAccessor {


    boolean invokeDoesBlockCauseSignalFire(BlockState state);
}
