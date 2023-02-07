package mbtw.mbtw.screen;

import mbtw.mbtw.Mbtw;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PropertyDelegate;

public class MillstoneScreenHandler extends AbstractMechanicalScreenHandler {
    public MillstoneScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(Mbtw.MILLSTONE_SCREEN_HANDLER, Mbtw.MILLING, syncId, playerInventory);
    }

    public MillstoneScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Mbtw.MILLSTONE_SCREEN_HANDLER, Mbtw.MILLING, syncId, playerInventory, inventory, propertyDelegate);
    }

}
