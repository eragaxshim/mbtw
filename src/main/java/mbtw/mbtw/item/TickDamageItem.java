package mbtw.mbtw.item;

import mbtw.mbtw.world.ItemTickManager1;
import mbtw.mbtw.world.ServerWorldMixinAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TickDamageItem extends Item {
    private final ItemStack targetItem;
    private final boolean doesDamageDecrease;

    public TickDamageItem(Settings settings, int decayTime, ItemStack targetItem, boolean doesDamageDecrease) {
        super(settings.maxDamage(decayTime));
        this.targetItem = targetItem;
        this.doesDamageDecrease = doesDamageDecrease;
    }

    public TickDamageItem(Settings settings, ItemStack targetItem, int decayTime) {
        super(settings.maxDamage(decayTime));
        this.targetItem = targetItem;
        this.doesDamageDecrease = false;
    }

    public void tick(ItemStack stack, World world, BlockPos pos)
    {
        //System.out.println();
        //System.out.println(world.isClient + "T" + stack.getDamage());
        //System.out.println();
        if (stack.getDamage() == (this.doesDamageDecrease ? 1 : stack.getMaxDamage() - 1))
        {
            this.onFinalDamage(stack, world);
        }
        else if (stack.getDamage() != 0)
        {
            stack.setDamage(MathHelper.clamp(stack.getDamage() + (this.doesDamageDecrease ? -23 : 23), 1, stack.getMaxDamage() - 1));
        }
        else {
            stack.setDamage(this.doesDamageDecrease ? stack.getMaxDamage() - 1 : 1);
        }
    }

    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient)
        {
            //System.out.println("IT" + world.isClient + " " + stack.getDamage());
        }
        if (stack.getDamage() == (this.doesDamageDecrease ? 1 : stack.getMaxDamage() - 1))
        {
            this.onFinalDamage(stack, world);
            return;
        }
        else if (stack.getDamage() != 0)
        {
            stack.setDamage(stack.getDamage() + (this.doesDamageDecrease ? -1 : 1));
        }
        else {
            stack.setDamage(this.doesDamageDecrease ? stack.getMaxDamage() - 1 : 1);
        }

        /*
        if (!world.isClient)
        {
            ItemTickManager1 itemTickManager = ((ServerWorldMixinAccessor) world).getItemTickManager();
            if (this.doesDamageDecrease ? stack.getDamage() <= 1 : stack.getDamage() >= stack.getMaxDamage())
            {
                stack.decrement(1);
                itemTickManager.removeTrackedStack(stack);
                return;
            }

            CompoundTag stackTag = stack.getOrCreateTag();

            int remainingTime = itemTickManager.getRemainingTime(stack, entity.getBlockPos());
            if (remainingTime == -1 && (!stackTag.contains("NoTickDecay") || (stackTag.contains("NoTickDecay") && !stackTag.getBoolean("NoTickDecay"))))
            {
                itemTickManager.addTrackedStack(stack, entity.getBlockPos(), stack.getMaxDamage());
            }
            else {
                stack.setDamage(this.doesDamageDecrease ? remainingTime + 1 : stack.getMaxDamage() - remainingTime);
            }
        }

         */
    }

    public void onFinalDamage(ItemStack stack, World world)
    {
        /*
        if (!world.isClient)
        {
            ((ServerWorldMixinAccessor) world).getItemTickManager().removeTrackedStack(stack);
        }
        */
        stack.decrement(1);
    }
}
