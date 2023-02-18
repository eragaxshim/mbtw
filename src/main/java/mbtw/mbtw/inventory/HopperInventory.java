package mbtw.mbtw.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;

public interface HopperInventory extends BlockStateInventory {
    Block getFilter();

    void setFilter(Block filter);
}
