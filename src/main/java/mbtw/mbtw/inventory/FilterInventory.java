package mbtw.mbtw.inventory;

import net.minecraft.block.Block;

public interface FilterInventory extends BlockStateInventory {
    Block getFilter();

    void setFilter(Block filter);
}
