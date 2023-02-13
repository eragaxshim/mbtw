package mbtw.mbtw.screen;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.recipe.AbstractMechanicalRecipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class CrucibleScreenHandler extends AbstractProcessorScreenHandler {
    public CrucibleScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(Mbtw.CRUCIBLE_SCREEN_HANDLER, Mbtw.CRUCIBLE_SMELTING, syncId, 10, playerInventory);
    }

    public CrucibleScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Mbtw.CRUCIBLE_SCREEN_HANDLER, Mbtw.CRUCIBLE_SMELTING, syncId, 10, playerInventory, inventory, propertyDelegate);
    }
}
