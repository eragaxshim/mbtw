package mbtw.mbtw.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface BlockStateInventory extends Inventory {
    BlockState getConnectedState(int index);

    void setConnectedState(int index, BlockState state);

    int connectedSize();

    Direction connectedDirection(int index);

    static void updateStates(World world, BlockPos pos, BlockStateInventory inventory) {
        for (int i = 0; i < inventory.connectedSize(); i++) {
            Direction direction = inventory.connectedDirection(i);
            inventory.setConnectedState(i, world.getBlockState(pos.offset(direction)));
        }
    }
}
