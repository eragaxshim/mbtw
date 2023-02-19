package mbtw.mbtw.inventory;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface FilterInventory extends BlockStateInventory {
    void putInFilter(ItemStack stack);

    ItemStack inFilter();

    void clearInFilter();

    Block getFilter();

    void setFilter(Block filter);
}
