package mbtw.mbtw.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public interface TickDamageItem {
    default void tick(ItemStack stack, World world, BlockPos pos)
    {
        boolean doesDamageDecrease = stack.getOrCreateTag().getBoolean("DecreaseDamageTick");
        if (stack.getDamage() == (doesDamageDecrease? 1 : stack.getMaxDamage() - 1))
        {
            this.onFinalDamage(stack, world);
        }
        else if (stack.getDamage() != 0)
        {
            stack.setDamage(MathHelper.clamp(stack.getDamage() + (doesDamageDecrease ? -23 : 23), 1, stack.getMaxDamage() - 1));
        }
        else {
            stack.setDamage(doesDamageDecrease ? stack.getMaxDamage() - 1 : 1);
        }
    }

    default void onFinalDamage(ItemStack stack, World world)
    {
        stack.decrement(1);
    }
}
