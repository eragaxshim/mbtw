package mbtw.mbtw.item;

import mbtw.mbtw.block.Ignitable;
import mbtw.mbtw.block.IgnitionProvider;
import mbtw.mbtw.block.entity.FiniteTorchBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FiniteTorchItem extends VerticallyAttachableBlockItem implements TickProgressable, Extinguishable {
    private final int burnTime;

    public FiniteTorchItem(Block standingBlock, Block wallBlock, Settings settings, int burnTime) {
        super(standingBlock, wallBlock, settings, Direction.DOWN);
        this.burnTime = burnTime;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState useState = context.getWorld().getBlockState(context.getBlockPos());
        if (context.getStack().getOrCreateSubNbt("BlockStateTag").getInt("torch_fire") > 1 && useState.getBlock() instanceof Ignitable && ((Ignitable) useState.getBlock()).ignite(context.getWorld(), context.getPlayer(), context.getStack(), useState, context.getBlockPos()))
        {
            return ActionResult.success(context.getWorld().isClient);
        }
        else if (context.getStack().getOrCreateSubNbt("BlockStateTag").getInt("torch_fire") == 0 && useState.getBlock() instanceof IgnitionProvider && ((IgnitionProvider) useState.getBlock()).canIgniteItem(context.getStack(), useState))
        {
            if (!context.getWorld().isClient)
            {
                ItemStack litTorch = context.getStack().split(1);
                litTorch.getOrCreateSubNbt("BlockStateTag").putInt("torch_fire", 3);
                litTorch.getOrCreateNbt().putBoolean("DecreaseProgressTick", true);
                litTorch.getOrCreateNbt().putInt("Progress", this.getMaxProgress());
                this.tick(litTorch, context.getWorld(), context.getBlockPos(), 1.0F, null);
                context.getWorld().playSound(null, context.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (context.getWorld().getRandom().nextFloat() - context.getWorld().getRandom().nextFloat()) * 0.2F + 1.0F);
                if (context.getPlayer() != null)
                {
                    context.getPlayer().getInventory().offerOrDrop(litTorch);
                }
                else {
                    Block.dropStack(context.getWorld(), context.getBlockPos(), litTorch);
                }
            }
            return ActionResult.success(context.getWorld().isClient);
        }
        return super.useOnBlock(context);
    }

    public void tick(ItemStack stack, World world, BlockPos pos, float tickModifier, @Nullable Entity holder)
    {
        if (stack.getItem() instanceof FiniteTorchItem)
        {
            NbtCompound stackTag = stack.getOrCreateNbt();

            if (!stackTag.contains("TickProgress"))
            {
                stackTag.putBoolean("TickProgress", false);
            }
            if (!stackTag.contains("Progress"))
            {
                stackTag.putInt("Progress", 0);
            }

            TickProgressable.super.tick(stack, world, pos, tickModifier, holder);

            NbtCompound blockStateTag = stack.getOrCreateSubNbt("BlockStateTag");
            int torchFire = blockStateTag.getInt("torch_fire");
            int newTorchFire = torchFire;
            if (stackTag.getBoolean("TickProgress"))
            {
                newTorchFire = FiniteTorchBlockEntity.calculateTorchFire(stackTag.getInt("Progress"), stackTag.getInt("MaxProgress"));
            }
            else if (torchFire > 1) {
                stackTag.putBoolean("TickProgress", true);
            }
            blockStateTag.putInt("torch_fire", newTorchFire);
            stackTag.put("BlockStateTag", blockStateTag);
            stack.getOrCreateSubNbt("BlockEntityTag").putInt("BurnTime", stackTag.getInt("Progress"));
        }
    }

    public int getMaxProgress() {
        return this.burnTime;
    }

    @Override
    public void onFinalProgress(ItemStack stack, World world, BlockPos pos, boolean doesProgressDecrease, int maxProgress)
    {
        TickProgressable.super.onFinalProgress(stack, world, pos, doesProgressDecrease, maxProgress);
        stack.getOrCreateSubNbt("BlockStateTag").putInt("torch_fire", 1);
        stack.getOrCreateSubNbt("BlockEntityTag").putInt("BurnTime", 0);
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
    }

    @Override
    public void extinguish(ItemStack stack, World world, BlockPos pos) {
        NbtCompound stackTag = stack.getOrCreateNbt();
        if (stackTag.getInt("Progress") != 0 || stack.getOrCreateSubNbt("BlockStateTag").getInt("torch_fire") > 1)
        {
            this.onFinalProgress(stack, world, pos, stackTag.getBoolean("DecreaseProgressTick"), stackTag.getInt("MaxProgress"));
        }
    }
}
