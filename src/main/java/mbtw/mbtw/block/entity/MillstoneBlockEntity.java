package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.*;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MillstoneBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos sourcePos;
    @Nullable
    private BlockState sourceState;

    private boolean processing;

    private BlockPos connectorPos;
    private BlockState connectorState;
    private int availablePower;

    private static final MechanicalSink SINK = (MechanicalSink) Mbtw.MILLSTONE;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.MILLSTONE_ENTITY, pos, state);
        this.sourcePos = null;
        this.sourceState = null;
        this.processing = true;
        this.connectorPos = null;
        this.connectorState = null;
    }

    public static void powerOff(World world, BlockPos pos, BlockState state, boolean updatedState) {
        if (state.get(MbtwProperties.POWERED) || updatedState) {
            world.setBlockState(pos, state.with(MbtwProperties.POWERED, false), Block.NOTIFY_ALL);
        }
    }

    public static void powerOn(World world, BlockPos pos, BlockState state, boolean updatedState) {
        if (!state.get(MbtwProperties.POWERED) || updatedState) {
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

    public static void updatePowered(World world, BlockPos sinkPos, BlockState sinkState, MillstoneBlockEntity millstone, boolean updatedState) {
        if (millstone.availablePower <= 0 || !millstone.processing) {
            powerOff(world, sinkPos, sinkState, updatedState);
        } else {
            powerOn(world, sinkPos, sinkState, updatedState);
        }
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, MillstoneBlockEntity millstone) {
        BlockState cState = millstone.connectorState;
        BlockPos cPos = millstone.connectorPos;
        // connectorPos and connectorState are set by block updates
        if (cPos != null && cState != null && cState.getBlock() instanceof MechanicalConnector connector) {
            int connectorSource = connector.getSource(cState);
            int connectorSink = connector.getSink(cState);
            if (connector.getBearing(cState)) {
                millstone.availablePower = connectorSink;
            } else {
                millstone.availablePower = 0;
            }

            int sink = SINK.getSink(sinkState);
            // If not sinking equal to source (and not already at max sink), set it to appropriate value
            if (sink != connectorSource && sink < SINK.getMaxSink()) {
                BlockState newState = sinkState.with(MillstoneBlock.MECHANICAL_SINK, Math.min(SINK.getMaxSink(), connectorSource));
                updatePowered(world, sinkPos, newState, millstone, true);
                return;
            }
        } else {
            millstone.availablePower = 0;
        }

        updatePowered(world, sinkPos, sinkState, millstone, false);

        if (world.getTime() % 43 == 0) {
            System.out.println("null millstone");
        }


//        if (sinkState.get(MbtwProperties.POWERED)) {
//
//        } else {
//
//
//            for (Direction direction : MillstoneBlock.VALID_INPUT_FACES) {
//                BlockPos cPos = sinkPos.offset(direction);
//                BlockState cState = world.getBlockState(cPos);
//                if (!(cState.getBlock() instanceof MechanicalConnector connector)) continue;
//                int source = connector.getSource(cState);
//                if (millstone.processing) {
//                    world.setBlockState(sinkPos, sinkState.with(MillstoneBlock.MECHANICAL_SINK, source));
//                }
//            }
//        }


//        if (world.getTime() % 21 != 0)  {
//            return;
//        }
//        System.out.println("srcPos " + millstone.sourcePos);
//        if (millstone.sourcePos == null) {
//            powerOff(world, sinkPos, sinkState);
//            return;
//        }
//
//        MechanicalSource source = MbtwApi.SOURCE_API.find(world, millstone.sourcePos, millstone.sourceState, null, null);
//        System.out.println(source);
//        if (source == null) {
//            millstone.sourceState = null;
//            millstone.sourcePos = null;
//            powerOff(world, sinkPos, sinkState);
//            System.out.println("srcNull");
//            return;
//        }
//        // Vector from source to sink
//        Vec3i sourceToSink = sinkPos.subtract(millstone.sourcePos);
//        // TODO cache for changes?
//        millstone.sourceState = world.getBlockState(millstone.sourcePos);
//        int out = source.getOutRotation(world, millstone.sourcePos, millstone.sourceState, sourceToSink);
//
//        if (out <= 0) {
//            powerOff(world, sinkPos, sinkState);
//            System.out.println("out Zero");
//            return;
//        }
//        System.out.println("on");
//
//        powerOn(world, sinkPos, sinkState);
    }
}
