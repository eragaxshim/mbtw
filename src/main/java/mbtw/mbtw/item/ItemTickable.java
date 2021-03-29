package mbtw.mbtw.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ItemTickable {
    void tick(ItemStack stack, World world, BlockPos pos, float tickModifier, @Nullable Entity holder);

    default void tick(ItemStack stack, World world, BlockPos pos, @Nullable Entity holder)
    {
        tick(stack, world, pos, 1.0F, holder);
    }
}
