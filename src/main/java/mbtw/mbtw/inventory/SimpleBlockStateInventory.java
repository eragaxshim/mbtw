package mbtw.mbtw.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class SimpleBlockStateInventory extends SimpleInventory implements BlockStateInventory {
    private final BlockState[] states;
    private static final Direction[] DIRECTIONS = new Direction[] {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public SimpleBlockStateInventory(int size) {
        super(size);
        this.states = new BlockState[6];
        Arrays.fill(states, null);
    }


    @Override
    public BlockState getConnectedState(int i) {
        return states[i];
    }

    @Override
    public void setConnectedState(int index, BlockState state) {

    }

    @Override
    public int connectedSize() {
        return 6;
    }

    @Override
    public Direction connectedDirection(int index) {
        return DIRECTIONS[index];
    }
}
