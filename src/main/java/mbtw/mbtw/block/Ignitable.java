package mbtw.mbtw.block;

import mbtw.mbtw.item.FireStarterItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Ignitable {
    default boolean attemptFireStart(World world, LivingEntity user, ItemStack stack, int meanStartTick, int remainingUseTick, BlockState block, BlockPos pos)
    {
        if (stack.getItem() instanceof FireStarterItem)
        {
            int fireStartTemp = stack.getMaxUseTime() - remainingUseTick;
            int requiredTemp;

            CompoundTag stackTag = stack.getOrCreateTag();

            boolean clear = false;
            if (!stackTag.contains("StartPos"))
            {
                stackTag.put("StartPos", NbtHelper.fromBlockPos(pos));
            }
            else {
                if (!NbtHelper.toBlockPos(stackTag.getCompound("StartPos")).equals(pos))
                {
                    stackTag.put("StartPos", NbtHelper.fromBlockPos(pos));
                    user.clearActiveItem();
                    clear = true;
                }
            }

            if (!stackTag.contains("RequiredTemp"))
            {
                float adjustedStartTick = meanStartTick * getStartTickFactor(block);
                requiredTemp = Math.min((int) Math.max(world.getRandom().nextGaussian()*(adjustedStartTick / 6 + 1) + adjustedStartTick, 0), stack.getMaxDamage() - 5);
                stackTag.putInt("RequiredTemp", requiredTemp);
            }
            else if (clear) {
                stackTag.remove("RequiredTemp");
                return false;
            }
            else {
                requiredTemp = stackTag.getInt("RequiredTemp");
            }

            if (fireStartTemp > requiredTemp)
            {
                ignite(world, user, stack, block, pos);
                stackTag.remove("RequiredTemp");
            }
            return true;
        }
        return false;
    }

    float getStartTickFactor(BlockState state);

    boolean ignite(World world, LivingEntity entity, ItemStack stack, BlockState block, BlockPos pos);
}
