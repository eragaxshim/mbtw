package mbtw.mbtw.block;

import mbtw.mbtw.item.FireStarterItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Igniteable {
    default boolean attemptFireStart(World world, LivingEntity entity, ItemStack stack, BlockState block, BlockPos pos)
    {
        if (stack.getItem() instanceof FireStarterItem)
        {
            float startEfficiency = ((FireStarterItem)stack.getItem()).getStartEfficiency();
            if (startEfficiency - world.getRandom().nextFloat() > 0)
            {
                ignite(world, entity, stack, block, pos);
            }
            return true;
        }
        return false;
    }

    void ignite(World world, LivingEntity entity, ItemStack stack, BlockState block, BlockPos pos);
}
