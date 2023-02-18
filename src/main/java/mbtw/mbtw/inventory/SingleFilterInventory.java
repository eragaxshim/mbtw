package mbtw.mbtw.inventory;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

public class SingleFilterInventory extends SingleBlockStateInventory implements FilterInventory {
    private Block filter;

    public SingleFilterInventory(int size, Direction direction) {
        super(size, direction);
        this.filter = Blocks.AIR;
    }

    public void setFilter(Block filter) {
        this.filter = filter;
    }

    @Override
    public Block getFilter() {
        return filter;
    }
}
