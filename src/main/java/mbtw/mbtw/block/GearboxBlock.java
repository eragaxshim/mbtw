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

import java.util.Optional;

public class GearboxBlock extends Block implements MechanicalSource {
    public static final Direction OUT_DIRECTION = Direction.UP;

    public GearboxBlock(Settings settings) {
        super(settings);
    }

    @Override
    public int getOutRotation(World world, BlockPos pos, BlockState state, Vec3i sourceToSink) {
        if (MechanicalVec.vecSameDirection(sourceToSink, OUT_DIRECTION)) {
            return 1;
        }
        return 0;
    }

    public static Optional<BlockPos> lookForSink(World world, BlockPos sourcePos, BlockState sourceState) {
        System.out.println("looking!");
        for (BlockPos iPos : BlockPos.iterate(sourcePos, sourcePos.offset(OUT_DIRECTION, 6))) {
            BlockState iState = world.getBlockState(iPos);
            System.out.println(iState);
            if (!(iState.getBlock() instanceof MechanicalSink)) continue;
            MechanicalSink sink = MbtwApi.SINK_API.find(world, iPos, iState, null, null);
            if (sink == null) continue;
            MechanicalVec rotationVector = new MechanicalVec(OUT_DIRECTION);
            if (!sink.addSource(world, rotationVector, sourcePos, sourceState, iPos, iState)) continue;

            return Optional.of(iPos);
        }
        return Optional.empty();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        System.out.println("Placed!");
        if (!world.isClient) {
            Optional<BlockPos> possibleSink = lookForSink(world, pos, state);
        }
    }
}
