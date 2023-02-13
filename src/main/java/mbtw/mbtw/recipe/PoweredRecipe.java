package mbtw.mbtw.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;

public interface PoweredRecipe extends Recipe<Inventory> {
    int getProcessingTime();

    int getRequiredPower();

    void decrementInput(DefaultedList<ItemStack> inputStacks);
}
