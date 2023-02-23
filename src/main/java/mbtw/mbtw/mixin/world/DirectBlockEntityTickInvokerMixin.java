package mbtw.mbtw.mixin.world;

import mbtw.mbtw.item.ItemTickable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.DirectBlockEntityTickInvoker.class)
public abstract class DirectBlockEntityTickInvokerMixin<T extends BlockEntity> {
    @Shadow @Final private T blockEntity;
    // Synthetic field referencing enclosing class, ignore warning
    @Shadow @Final
    WorldChunk worldChunk;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;)V"))
    public void changeTick(CallbackInfo ci) {
        World world = worldChunk.getWorld();
        if (!world.isClient && (world.getTime() % 23) == 0 && blockEntity instanceof LockableContainerBlockEntity && !(blockEntity instanceof ShulkerBoxBlockEntity)) {

            Inventory blockEntityInventory = (Inventory) blockEntity;
            int size = blockEntityInventory.size();
            for (int i = 0; i < size; i++) {
                ItemStack stack = blockEntityInventory.getStack(i);
                if (stack.getItem() instanceof ItemTickable)
                {
                    ((ItemTickable)stack.getItem()).tick(stack, world, blockEntity.getPos(), null);
                }
            }
        }
    }
}
