package mbtw.mbtw.mixin.entity.player;

import mbtw.mbtw.item.ItemTickable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Stream;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow public abstract boolean insertStack(ItemStack stack);

    @Inject(method = "updateItems", at = @At("HEAD"))
    protected void changeUpdateItems(CallbackInfo ci)
    {
        if (this.player.world.getTime() % 23 == 0 && !this.player.world.isClient)
        {
            DefaultedList<ItemStack> tickableStacks = DefaultedList.of();
            ItemStack cursorStack = this.player.currentScreenHandler.getCursorStack();
            if (cursorStack.getItem() instanceof ItemTickable) {
                tickableStacks.add(cursorStack);
            }
            tickableStacks.addAll(this.player.currentScreenHandler.slots.stream().flatMap(slot -> {
                ItemStack slotStack = slot.getStack();
                if (slotStack.getItem() instanceof ItemTickable) {
                    return Stream.of(slotStack);
                } else {
                    return Stream.empty();
                }
            }).toList());
            for (ItemStack stack : tickableStacks)
            {
                ((ItemTickable)stack.getItem()).tick(stack, this.player.world, this.player.getBlockPos(), this.player);
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
