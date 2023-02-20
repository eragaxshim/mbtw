package mbtw.mbtw.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtil {
    public static void spawnItem(World world, ItemStack stack, BlockPos pos, double yHeight) {
        double x = pos.getX() + world.random.nextTriangular(0.5, 0.03);
        double y = pos.getY() + world.random.nextTriangular(yHeight, 0.03);
        double z = pos.getZ() + world.random.nextTriangular(0.5, 0.03);

        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
        itemEntity.setVelocity(
                world.random.nextTriangular(0, 0.0172275),
                world.random.nextTriangular(0.35, 0.0172275*3),
                world.random.nextTriangular(0, 0.0172275)
        );
        world.spawnEntity(itemEntity);
    }
}
