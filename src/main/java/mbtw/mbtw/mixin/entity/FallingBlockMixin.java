package mbtw.mbtw.mixin.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockMixin {
    @Accessor("block")
    BlockState getBlock();

    @Accessor("block")
    void setBlock(BlockState block);

    @Accessor("destroyedOnLanding")
    boolean getDestroyedOnLanding();
}
