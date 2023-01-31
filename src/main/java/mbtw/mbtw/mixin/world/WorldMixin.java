package mbtw.mbtw.mixin.world;

import com.google.common.collect.Lists;
import mbtw.mbtw.item.ItemTickable;
import mbtw.mbtw.mixin.block.DirectBlockEntityTickInvokerAccessor;
import mbtw.mbtw.mixin.block.WrappedBlockEntityTickInvokerAccessor;
import mbtw.mbtw.mixin.screen.CraftingContextAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess {
    @Shadow public abstract long getTime();
    @Shadow @Final public boolean isClient;

    @Shadow public abstract @Nullable BlockEntity getBlockEntity(BlockPos pos);

    @Shadow @Final protected List<BlockEntityTickInvoker> blockEntityTickers;

    @Shadow @Final private List<BlockEntityTickInvoker> pendingBlockEntityTickers;

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), locals=LocalCapture.CAPTURE_FAILEXCEPTION)
    protected void changeTickBlockEntities(CallbackInfo ci)
    {
          if (!this.isClient && (this.getTime() % 43) == 0) {
              for (BlockEntityTickInvoker tickInvoker : this.blockEntityTickers) {
                  if (tickInvoker instanceof WorldChunk.WrappedBlockEntityTickInvoker) {
                      tickInvoker = ((WrappedBlockEntityTickInvokerAccessor)tickInvoker).getWrapped();
                  }
                  if (tickInvoker instanceof WorldChunk.DirectBlockEntityTickInvoker<?>) {
                      BlockEntity blockEntity = ((DirectBlockEntityTickInvokerAccessor<?>) tickInvoker).getBlockEntity();
                      //System.out.println(blockEntity);
                  } else {
                      //System.out.println(tickInvoker);
                  }


              }

              for (BlockEntityTickInvoker tickInvoker : this.pendingBlockEntityTickers) {
                  if (tickInvoker instanceof WorldChunk.WrappedBlockEntityTickInvoker) {
                      tickInvoker = ((WrappedBlockEntityTickInvokerAccessor)tickInvoker).getWrapped();
                  }
                  if (tickInvoker instanceof WorldChunk.DirectBlockEntityTickInvoker<?>) {
                      BlockEntity blockEntity = ((DirectBlockEntityTickInvokerAccessor<?>) tickInvoker).getBlockEntity();
                      System.out.println("pending ".concat(blockEntity.toString()));
                  } else {
                      System.out.println("pending not".concat(tickInvoker.toString()));
                  }
              }
              //System.out.println(this.blockEntityTickers)
          }
//        if (!this.isClient && this.getTime() % 23 == 0)
//        {
//            if (blockEntityTickInvoker instanceof WorldChunk.DirectBlockEntityTickInvoker<?>) {
//                BlockEntity blockEntity = ((DirectBlockEntityTickInvokerAccessor<?>) blockEntityTickInvoker).getBlockEntity();
//                if (blockEntity instanceof Inventory) {
//                    System.out.println("Found inventory");
//                    int size = ((Inventory) blockEntity).size();
//                    for (int i = 0; i < size; i++) {
//                        System.out.println(((Inventory) blockEntity).getStack(i));
//                    }
//                }
//            }
//        }
    }

}
