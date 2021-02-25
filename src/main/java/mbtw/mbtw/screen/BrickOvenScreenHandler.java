package mbtw.mbtw.screen;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.mixin.SlotIndexAccessor;
import mbtw.mbtw.screen.slot.LimitedItemSlot;
import mbtw.mbtw.screen.slot.OvenFuelSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;

public class BrickOvenScreenHandler extends AbstractFurnaceScreenHandler {
    public BrickOvenScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(Mbtw.BRICK_OVEN_SCREEN_HANDLER, Mbtw.BRICK_SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory);
    }

    public BrickOvenScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(Mbtw.BRICK_OVEN_SCREEN_HANDLER, Mbtw.BRICK_SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
    }



    @Override
    protected Slot addSlot(Slot slot) {
        int i = ((SlotIndexAccessor)slot).getIndex();
        if (!(slot.inventory instanceof PlayerInventory))
        {
            if (i == 0)
            {
                slot = new LimitedItemSlot(slot.inventory, i, slot.x, slot.y, 1);
            }
            else if (i == 1)
            {
                slot = new OvenFuelSlot(this, slot.inventory, i, slot.x, slot.y);
            }
        }

        return super.addSlot(slot);
    }
}
