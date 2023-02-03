package mbtw.mbtw.block;

import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GearboxBlock extends Block implements MechanicalSource {
    public static final Direction OUT_DIRECTION = Direction.UP;

    public GearboxBlock(Settings settings) {
        super(settings);
    }

    @Override
    public List<MechanicalVec> getOutVecs(BlockPos sourcePos, BlockState sourceState) {
        return List.of(new MechanicalVec(OUT_DIRECTION));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        System.out.println("Placed!");
        if (!world.isClient) {
            Optional<BlockPos> possibleSink = lookForSink(world, pos, state);
        }
    }
}
