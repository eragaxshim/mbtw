package mbtw.mbtw.mixin.world;

import mbtw.mbtw.block.ContainerTicker;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    // Ignore error
    @ModifyVariable(method = "updateTicker", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlockEntityTicker(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/BlockEntityType;)Lnet/minecraft/block/entity/BlockEntityTicker;"), name = "blockEntityTicker")
    protected BlockEntityTicker<?> changeTicker(BlockEntityTicker<?> blockEntityTicker, BlockEntity blockEntity) {
        if (blockEntity.getWorld() == null) {
            return blockEntityTicker;
        }

        if (blockEntityTicker == null && !blockEntity.getWorld().isClient && blockEntity instanceof LockableContainerBlockEntity && !(blockEntity instanceof ShulkerBoxBlockEntity)) {
            return ContainerTicker::tick;
        }
        return blockEntityTicker;
    }
}
