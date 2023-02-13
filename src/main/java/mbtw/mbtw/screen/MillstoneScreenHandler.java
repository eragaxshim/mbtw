package mbtw.mbtw.screen;

import mbtw.mbtw.Mbtw;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PropertyDelegate;

public class MillstoneScreenHandler extends AbstractProcessorScreenHandler {
    public MillstoneScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(Mbtw.MILLSTONE_SCREEN_HANDLER, Mbtw.MILLING, syncId, 2, playerInventory);
    }

    public MillstoneScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Mbtw.MILLSTONE_SCREEN_HANDLER, Mbtw.MILLING, syncId, 2, playerInventory, inventory, propertyDelegate);
    }

}
