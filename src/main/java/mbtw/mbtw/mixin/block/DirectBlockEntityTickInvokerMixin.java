package mbtw.mbtw.mixin.block;

import mbtw.mbtw.item.ItemTickable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(WorldChunk.DirectBlockEntityTickInvoker.class)
public abstract class DirectBlockEntityTickInvokerMixin<T extends BlockEntity> {
    @Shadow @Final private T blockEntity;
    @Shadow @Final WorldChunk worldChunk;
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;)V"))
    public void changeTick(CallbackInfo ci) {
        //System.out.println("client? ".concat(String.valueOf(this.worldChunk.getWorld().isClient)));
        World world = this.worldChunk.getWorld();
        if (!world.isClient) {
            //System.out.println(blockEntity);
            //System.out.println(world.getTime() % 43);
        }

        if (!world.isClient && (world.getTime() % 43) == 0 && blockEntity instanceof ChestBlockEntity && !(blockEntity instanceof ShulkerBoxBlockEntity)) {
            //System.out.println(blockEntity.getPos());

            Inventory blockEntityInventory = (Inventory) blockEntity;
            int size = blockEntityInventory.size();
            for (int i = 0; i < size; i++) {
                //System.out.println(blockEntityInventory.getStack(i));
            }

//            Field inventoryField = null;
//
//            Class<?> clazz = blockEntity.getClass();
//            while (inventoryField == null) {
//
//                if (clazz == null) {
//                    break;
//                }
//                try {
//                    inventoryField = clazz.getDeclaredField("inventory");
//                } catch (NoSuchFieldException ignored) {
//                    clazz = clazz.getSuperclass();
//                }
//            }
//
//            try {
//                if (inventoryField != null) {
//                    System.out.println("TickINV2!");
//                    inventoryField.setAccessible(true);
//                    DefaultedList<ItemStack> inventory = (DefaultedList<ItemStack>) inventoryField.get(blockEntity);
//                    System.out.println("inventory");
//                    inventory.stream()
//                            .filter(stack -> {
//                                System.out.println(stack.getItem().getName());
//                                return stack.getItem() instanceof ItemTickable;
//                            })
//                            .forEach(stack -> {
//                                ((ItemTickable) stack.getItem()).tick(stack, blockEntity.getWorld(), blockEntity.getPos(), null);
//                                System.out.println("Ticked item");
//                            });
//                }
//            } catch (IllegalAccessException | ClassCastException e) {
//                System.out.println(e.toString());
//            }
        }
    }
}
