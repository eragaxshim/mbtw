package mbtw.mbtw.item;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Extinguishable {
    void extinguish(ItemStack stack, World world);
}
