package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.MbtwApi;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.block.MechanicalSource;
import mbtw.mbtw.block.MillstoneBlock;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MillstoneBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos sourcePos;
    @Nullable
    private BlockState sourceState;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.MILLSTONE_ENTITY, pos, state);
        this.sourcePos = null;
        this.sourceState = null;
    }

    public static void powerOff(World world, BlockPos pos, BlockState state) {
        if (state.get(MbtwProperties.POWERED)) {
            world.setBlockState(pos, state.with(MbtwProperties.POWERED, false), Block.NOTIFY_ALL);
        }
    }

    public static void powerOn(World world, BlockPos pos, BlockState state) {
        if (!state.get(MbtwProperties.POWERED)) {
            world.setBlockState(pos, state.with(MbtwProperties.POWERED, true), Block.NOTIFY_ALL);
        }
    }

    public boolean addSource(MechanicalVec rotVec, BlockPos sourcePos, BlockState sourceState, BlockPos pos, BlockState state) {
        // Check if previous one still exists?

        if (this.sourcePos == null && rotVec.oneOfDirecions(MillstoneBlock.VALID_INPUT_FACES)) {
            this.sourcePos = sourcePos;
            this.sourceState = sourceState;
            return true;
        }
        return false;
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, MillstoneBlockEntity millstone) {
        if (world.getTime() % 21 != 0)  {
            return;
        }
        System.out.println("srcPos " + millstone.sourcePos);
        if (millstone.sourcePos == null) {
            powerOff(world, sinkPos, sinkState);
            return;
        }

        MechanicalSource source = MbtwApi.SOURCE_API.find(world, millstone.sourcePos, millstone.sourceState, null, null);
        System.out.println(source);
        if (source == null) {
            millstone.sourceState = null;
            millstone.sourcePos = null;
            powerOff(world, sinkPos, sinkState);
            System.out.println("srcNull");
            return;
        }
        // Vector from source to sink
        Vec3i sourceToSink = sinkPos.subtract(millstone.sourcePos);
        int out = source.getOutRotation(world, millstone.sourcePos, sinkState, sourceToSink);
        // If we haven't saved the state, do so
        if (millstone.sourceState == null) {
            millstone.sourceState = world.getBlockState(millstone.sourcePos);
        }
        if (out <= 0) {
            powerOff(world, sinkPos, sinkState);
            System.out.println("out Zero");
            return;
        }
        System.out.println("on");

        powerOn(world, sinkPos, sinkState);
    }
}
