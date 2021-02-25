package mbtw.mbtw.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class InsertItemMixin {
    @Shadow @Final public List<Slot> slots;

    /**
     * @author EradurGwath
     */
    @Overwrite
    public boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }

        Slot slot2;
        ItemStack slotStack;
        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot2 = (Slot)this.slots.get(i);
                slotStack = slot2.getStack();

                if (!slotStack.isEmpty() && ScreenHandler.canStacksCombine(stack, slotStack) && slotStack.getCount() < slot2.getMaxItemCount()) {
                    int j = slotStack.getCount() + stack.getCount();
                    int maxSlotCount = Math.min(stack.getMaxCount(), slot2.getMaxItemCount());
                    if (j <= maxSlotCount) {
                        stack.setCount(0);
                        slotStack.setCount(j);
                        slot2.markDirty();
                        bl = true;
                    } else if (slotStack.getCount() < maxSlotCount) {
                        stack.decrement(maxSlotCount - slotStack.getCount());
                        slotStack.setCount(maxSlotCount);
                        slot2.markDirty();
                        bl = true;
                    }
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        if (!stack.isEmpty()) {
            if (fromLast) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot2 = (Slot)this.slots.get(i);
                slotStack = slot2.getStack();

                if (slotStack.isEmpty() && slot2.canInsert(stack) && slotStack.getCount() < slot2.getMaxItemCount()) {
                    if (stack.getCount() > slot2.getMaxItemCount()) {
                        slot2.setStack(stack.split(slot2.getMaxItemCount()));
                    } else {
                        slot2.setStack(stack.split(stack.getCount()));
                    }

                    slot2.markDirty();
                    bl = true;
                    break;
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return bl;
    }
}
