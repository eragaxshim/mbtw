package mbtw.mbtw.mixin.entity.player;

import mbtw.mbtw.item.TickDamageItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow private ItemStack cursorStack;

    @Shadow @Final public PlayerEntity player;

    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow public abstract boolean insertStack(ItemStack stack);

    @Inject(method = "updateItems", at = @At("HEAD"))
    protected void changeUpdateItems(CallbackInfo ci)
    {
        if (this.player.world.getTime() % 10 == 0)
        {
            Field inventoryField = null;

            Class<?> clazz = this.player.currentScreenHandler.getClass();
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

            DefaultedList<ItemStack> initial = DefaultedList.of();
            initial.add(cursorStack);
            DefaultedList<ItemStack> tickableStacks = this.combinedInventory.stream().reduce(initial, (subTotal, element) -> {
                subTotal.addAll(element);
                return subTotal;
            });

            if (inventoryField == null)
            {
                tickableStacks.addAll(this.player.currentScreenHandler.slots.stream().map((Slot::getStack)).collect(Collectors.toList()));
            }

            for (ItemStack stack : tickableStacks)
            {
                if (stack.getItem() instanceof TickDamageItem)
                {
                    ((TickDamageItem)stack.getItem()).tick(stack, this.player.world, this.player.getBlockPos());
                }
            }
            /*
            if (!this.cursorStack.isEmpty() && this.cursorStack.getItem() instanceof TickDamageItem)
            {
                ((TickDamageItem)this.cursorStack.getItem()).tick(this.cursorStack, this.player.world, this.player.getBlockPos());
            }

             */
        }
    }
}
