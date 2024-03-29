package mbtw.mbtw.screen;

import mbtw.mbtw.recipe.AbstractMechanicalRecipe;
import mbtw.mbtw.recipe.PoweredRecipe;
import mbtw.mbtw.screen.slot.MechanicalOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class AbstractProcessorScreenHandler extends AbstractRecipeScreenHandler<Inventory> {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeType<? extends PoweredRecipe> recipeType;
    private final int inventorySize;

    protected AbstractProcessorScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends PoweredRecipe> recipeType, int syncId, int inventorySize, PlayerInventory playerInventory) {
        this(type, recipeType, syncId, inventorySize, playerInventory, new SimpleInventory(inventorySize), new ArrayPropertyDelegate(3));
    }

    protected AbstractProcessorScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends PoweredRecipe> recipeType, int syncId, int inventorySize, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        this.inventorySize = inventorySize;
        this.recipeType = recipeType;
        AbstractProcessorScreenHandler.checkSize(inventory, inventorySize);
        AbstractProcessorScreenHandler.checkDataCount(propertyDelegate, 2);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;
        this.addSlots(inventory, playerInventory);
        this.addPlayerSlots(playerInventory);
        this.addProperties(propertyDelegate);
    }

    public void addPlayerSlots(PlayerInventory playerInventory) {
        // Copied from AbstractFurnaceScreenHandler
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public void addSlots(Inventory inventory, PlayerInventory playerInventory) {
        this.addSlot(new MechanicalOutputSlot(playerInventory.player, inventory, 0, 116, 35));
        if (inventory.size() > 2) {
            for(int i = 0; i < 3; ++i) {
                for(int j = 0; j < 3; ++j) {
                    if (j + i * 3 < inventory.size()-1) {
                        this.addSlot(new Slot(inventory, 1 + j + i * 3, 30 + j * 18, 17 + i * 18));
                    }
                }
            }
        } else {
            this.addSlot(new Slot(inventory, 1, 56, 17));
        }
    }

    public boolean isPowered() {
        return this.propertyDelegate.get(2) > 0;
    }

    public int getCookProgress() {
        int i = this.propertyDelegate.get(0);
        int j = this.propertyDelegate.get(1);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 24 / j;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {

    }

    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
    }

    @Override
    public boolean matches(Recipe<? super Inventory> recipe) {
        return recipe.matches(this.inventory, this.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return inventorySize > 2 ? 3 : 1;
    }

    @Override
    public int getCraftingHeight() {
        return inventorySize > 2 ? 3 : 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return inventorySize;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return null;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != 0;
    }
}
