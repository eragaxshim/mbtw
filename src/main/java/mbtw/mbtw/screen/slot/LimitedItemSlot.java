package mbtw.mbtw.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class LimitedItemSlot extends Slot {
    private final int itemCountLimit;

    public LimitedItemSlot(Inventory inventory, int index, int x, int y, int itemCountLimit) {
        super(inventory, index, x, y);
        this.itemCountLimit = itemCountLimit;
    }

    public int getMaxItemCount() {
        return Math.min(this.inventory.getMaxCountPerStack(), itemCountLimit);
    }

    public int getMaxItemCount(ItemStack stack) {
        return this.getMaxItemCount();
    }
}
