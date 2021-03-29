package mbtw.mbtw.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public interface TickDamageItem {
    default void tick(ItemStack stack, World world, BlockPos pos)
    {
        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        if (stack.getOrCreateTag().getBoolean("TickDamage"))
        {
            boolean doesDamageDecrease = stack.getOrCreateTag().getBoolean("DecreaseDamageTick");
            if (damage == (doesDamageDecrease? 1 : stack.getMaxDamage() - 1))
            {
                this.onFinalDamage(stack, world);
            }
            else if (damage != 0)
            {
                stack.setDamage(MathHelper.clamp(damage + (doesDamageDecrease ? -23 : 23), 1, maxDamage - 1));
            }
            else {
                stack.setDamage(doesDamageDecrease ? maxDamage - 1 : 1);
            }
        }
    }

    default void onFinalDamage(ItemStack stack, World world)
    {
        stack.decrement(1);
    }
}
