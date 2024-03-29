package mbtw.mbtw.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface TickProgressable extends ItemTickable {
    default void tick(ItemStack stack, World world, BlockPos pos, float tickModifier, @Nullable Entity holder)
    {
        NbtCompound stackTag = stack.getOrCreateNbt();
        int progress = stackTag.getInt("Progress");

        int maxProgress;
        if (!stackTag.contains("MaxProgress"))
        {
            maxProgress = getMaxProgress();
            stackTag.putInt("MaxProgress", maxProgress);
        }
        else {
            maxProgress = stackTag.getInt("MaxProgress");
        }
        if (stack.getOrCreateNbt().getBoolean("TickProgress"))
        {
            boolean doesProgressDecrease = stack.getOrCreateNbt().getBoolean("DecreaseProgressTick");
            if ((doesProgressDecrease && progress <= 1) || (!doesProgressDecrease && progress >= maxProgress - 1))
            {
                this.onFinalProgress(stack, world, pos, doesProgressDecrease, maxProgress);
            }
            else if (progress != 0)
            {
                if (holder != null)
                {
                    tickModifier *= 3;
                }
                stackTag.putInt("Progress", MathHelper.clamp(progress + (doesProgressDecrease ? (int) (-23 * tickModifier) : (int) (23 * tickModifier)), 1, maxProgress - 1));
            }
        }
    }

    int getMaxProgress();

    default void onFinalProgress(ItemStack stack, World world, BlockPos pos, boolean doesProgressDecrease, int maxProgress)
    {
        stack.getOrCreateNbt().putInt("Progress", doesProgressDecrease ? 0 : maxProgress);
        stack.getOrCreateNbt().putBoolean("TickProgress", false);
    }
}
