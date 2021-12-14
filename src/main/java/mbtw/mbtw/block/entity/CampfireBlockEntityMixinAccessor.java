package mbtw.mbtw.block.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface CampfireBlockEntityMixinAccessor {
    ItemStack getFinishedStack();

    DefaultedList<ItemStack> getItemsBeingCooked();

    boolean[] getFinishedItems();

    int[] getCookingTimes();

    int[] getCookingTotalTimes();
}
