package mbtw.mbtw.mixin.world;

import mbtw.mbtw.item.TickDamageItem;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.Iterator;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public abstract long getTime();

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Tickable;tick()V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    protected void changeTickBlockEntities(CallbackInfo ci, Profiler profiler, Iterator iterator, BlockEntity blockEntity, BlockPos blockPos)
    {
        if (this.getTime() % 23 == 0)
        {
            System.out.println(blockEntity);
            if (blockEntity instanceof LockableContainerBlockEntity && !(blockEntity instanceof ShulkerBoxBlockEntity)) {
                Field inventoryField = null;

                Class<?> clazz = blockEntity.getClass();
                while (inventoryField == null) {

                    if (clazz == null) {
                        break;
                    }
                    try {
                        inventoryField = clazz.getDeclaredField("inventory");
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        System.out.println(clazz);
                        clazz = clazz.getSuperclass();
                        System.out.println(clazz);
                    }
                }

                try {
                    if (inventoryField != null) {
                        inventoryField.setAccessible(true);
                        DefaultedList<ItemStack> inventory = (DefaultedList<ItemStack>) inventoryField.get(blockEntity);
                        inventory.stream()
                                .filter(stack -> stack.getItem() instanceof TickDamageItem)
                                .forEach(stack -> ((TickDamageItem) stack.getItem()).tick(stack, blockEntity.getWorld(), blockPos));
                    }
                } catch (IllegalAccessException | ClassCastException ignored) { }

            }
        }

    }
}
