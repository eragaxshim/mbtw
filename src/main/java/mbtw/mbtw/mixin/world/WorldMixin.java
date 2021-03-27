package mbtw.mbtw.mixin.world;

import com.google.common.collect.Lists;
import mbtw.mbtw.item.TickDamageItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess {
    @Shadow public abstract long getTime();

    @Shadow @Final protected List<BlockEntity> unloadedBlockEntities;
    @Shadow protected boolean iteratingTickingBlockEntities;
    private final List<BlockEntity> containerBlockEntities = Lists.newArrayList();

    @Inject(method = "tickBlockEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;unloadedBlockEntities:Ljava/util/List;"))
    protected void tickContainerBlockEntities(CallbackInfo ci)
    {
        if (!this.unloadedBlockEntities.isEmpty())
        {
            this.containerBlockEntities.removeAll(this.unloadedBlockEntities);
        }
    }

    @Inject(method = "tickBlockEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;iteratingTickingBlockEntities:Z"))
    protected void changeTickBlockEntities(CallbackInfo ci)
    {
        if (this.getTime() % 23 == 0 && this.iteratingTickingBlockEntities)
        {
            for (BlockEntity blockEntity : this.containerBlockEntities)
            {
                if (blockEntity instanceof LockableContainerBlockEntity && !(blockEntity instanceof ShulkerBoxBlockEntity)) {
                    if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
                        BlockPos blockPos = blockEntity.getPos();
                        if (this.getChunkManager().shouldTickBlock(blockPos) && this.getWorldBorder().contains(blockPos)) {
                            Field inventoryField = null;

                            Class<?> clazz = blockEntity.getClass();
                            while (inventoryField == null) {

                                if (clazz == null) {
                                    break;
                                }
                                try {
                                    inventoryField = clazz.getDeclaredField("inventory");
                                } catch (NoSuchFieldException ignored) {
                                    clazz = clazz.getSuperclass();
                                }
                            }

                            try {
                                if (inventoryField != null) {
                                    inventoryField.setAccessible(true);
                                    DefaultedList<ItemStack> inventory = (DefaultedList<ItemStack>) inventoryField.get(blockEntity);
                                    inventory.stream()
                                            .filter(stack -> stack.getItem() instanceof TickDamageItem)
                                            .forEach(stack -> ((TickDamageItem) stack.getItem()).tick(stack, blockEntity.getWorld(), blockEntity.getPos()));
                                }
                            } catch (IllegalAccessException | ClassCastException ignored) { }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "addBlockEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    protected void changeAddBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir)
    {
        if (blockEntity instanceof LockableContainerBlockEntity)
        {
            this.containerBlockEntities.add(blockEntity);
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;blockEntities:Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    protected void changeRemoveBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity blockEntity)
    {
        this.containerBlockEntities.remove(blockEntity);
    }
}
