package mbtw.mbtw.mixin.block;

import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldChunk.WrappedBlockEntityTickInvoker.class)
public interface WrappedBlockEntityTickInvokerAccessor {
    @Accessor("wrapped")
    BlockEntityTickInvoker getWrapped();
}
