package mbtw.mbtw.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface RecipeMixinAccess {
    DefaultedList<ItemStack> getDropOutput();

    void setDropOutput(DefaultedList<ItemStack> dropOutput);
}
