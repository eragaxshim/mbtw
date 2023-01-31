package mbtw.mbtw.mixin.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldChunk.DirectBlockEntityTickInvoker.class)
public interface DirectBlockEntityTickInvokerAccessor<T extends BlockEntity> {
    @Accessor("blockEntity")
    T getBlockEntity();
}
