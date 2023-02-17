package mbtw.mbtw.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Direction;

public class SingleBlockStateInventory extends SimpleInventory implements BlockStateInventory {
   private BlockState state;
   private final Direction direction;

    public SingleBlockStateInventory(int size, Direction direction) {
        super(size);
        this.state = null;
        this.direction = direction;
    }


    @Override
    public BlockState getConnectedState(int index) {
        if (index == 0) {
            return state;
        } else {
            throw new IndexOutOfBoundsException("Only has 1 connected state.");
        }
    }

    @Override
    public void setConnectedState(int index, BlockState state) {
        if (index == 0) {
            this.state = state;
        } else {
            throw new IndexOutOfBoundsException("Only has 1 connected state.");
        }
    }

    @Override
    public int connectedSize() {
        return 1;
    }

    @Override
    public Direction connectedDirection(int index) {
        if (index == 0) {
            return direction;
        } else {
            throw new IndexOutOfBoundsException("Connected states only has size " + connectedSize());
        }
    }
}
