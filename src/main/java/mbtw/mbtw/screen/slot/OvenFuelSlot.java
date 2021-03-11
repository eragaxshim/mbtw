package mbtw.mbtw.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.FurnaceFuelSlot;

public class OvenFuelSlot extends FurnaceFuelSlot {
    private final AbstractFurnaceScreenHandler handler;

    public OvenFuelSlot(AbstractFurnaceScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(handler, inventory, index, x, y);
        this.handler = handler;
    }

    public boolean canInsert(ItemStack stack) {
        if (this.handler.isBurning() && this.handler.getFuelProgress() / 13.0f > 0.1)
        {
            return false;
        }

        return super.canInsert(stack);
    }

    public int getMaxItemCount() {
        return 1;
    }

    public int getMaxItemCount(ItemStack stack) {
        return this.getMaxItemCount();
    }
}
