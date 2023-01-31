package mbtw.mbtw.block;

import mbtw.mbtw.item.ItemTickable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerTicker {
    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
        if (world.getTime() % 23 == 0 && blockEntity instanceof LockableContainerBlockEntity containerEntity) {
            for (int i = 0; i < containerEntity.size(); i++) {
                ItemStack stack = containerEntity.getStack(i);
                if (stack.getItem() instanceof ItemTickable)
                {
                    ((ItemTickable)stack.getItem()).tick(stack, world, pos, null);
                }
            }
        }
    }
}
