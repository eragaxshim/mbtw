package mbtw.mbtw.block;

import mbtw.mbtw.state.property.MbtwProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BreakInterceptable {
    //public static final BooleanProperty BROKEN = MbtwProperties.BROKEN;

    BlockState processBreakAttempt(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack handStack);
}
