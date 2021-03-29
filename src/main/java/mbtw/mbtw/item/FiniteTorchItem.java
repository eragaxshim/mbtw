package mbtw.mbtw.item;

import mbtw.mbtw.block.Ignitable;
import mbtw.mbtw.block.entity.FiniteTorchBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FiniteTorchItem extends WallStandingBlockItem implements TickDamageItem, Extinguishable {
    public FiniteTorchItem(Block standingBlock, Block wallBlock, Settings settings, int burnTime) {
        super(standingBlock, wallBlock, settings.maxDamage(burnTime));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState useState = context.getWorld().getBlockState(context.getBlockPos());
        if (useState.getBlock() instanceof Ignitable && ((Ignitable) useState.getBlock()).ignite(context.getWorld(), context.getPlayer(), context.getStack(), useState, context.getBlockPos()))
        {
            return ActionResult.success(context.getWorld().isClient);
        }
        return super.useOnBlock(context);
    }

    public void tick(ItemStack stack, World world, BlockPos pos)
    {
        TickDamageItem.super.tick(stack, world, pos);

        if (stack.getItem() instanceof FiniteTorchItem)
        {
            CompoundTag stackTag = stack.getOrCreateTag();
            CompoundTag blockStateTag = stack.getOrCreateSubTag("BlockStateTag");
            int torchFire = blockStateTag.getInt("torch_fire");

            int newTorchFire = torchFire;
            if (stackTag.getBoolean("TickDamage"))
            {
                newTorchFire = FiniteTorchBlockEntity.calculateTorchFire(stack.getDamage(), stack.getMaxDamage());
            }
            else if (torchFire > 1) {
                stackTag.putBoolean("TickDamage", true);
            }
            blockStateTag.putInt("torch_fire", newTorchFire);
            stackTag.put("BlockStateTag", blockStateTag);
            stack.getOrCreateSubTag("BlockEntityTag").putInt("BurningTime", stack.getDamage());
        }
    }

    @Override
    public void onFinalDamage(ItemStack stack, World world)
    {
        stack.setDamage(0);
        stack.getOrCreateTag().putBoolean("TickDamage", false);
        stack.getOrCreateSubTag("BlockStateTag").putInt("torch_fire", 1);
        stack.getOrCreateSubTag("BlockEntityTag").putInt("BurningTime", 0);
    }

    @Override
    public void extinguish(ItemStack stack, World world) {
        if (stack.getDamage() != 0 || stack.getOrCreateSubTag("BlockStateTag").getInt("torch_fire") > 1)
        {
            this.onFinalDamage(stack, world);
        }
    }
}
