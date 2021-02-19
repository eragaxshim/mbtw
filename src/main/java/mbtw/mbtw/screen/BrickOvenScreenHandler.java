package mbtw.mbtw.screen;

import mbtw.mbtw.Mbtw;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class BrickOvenScreenHandler extends AbstractFurnaceScreenHandler {
    public BrickOvenScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(Mbtw.BRICK_OVEN_SCREEN_HANDLER, Mbtw.BRICK_SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory);
    }

    public BrickOvenScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Mbtw.BRICK_OVEN_SCREEN_HANDLER, Mbtw.BRICK_SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
    }
}
