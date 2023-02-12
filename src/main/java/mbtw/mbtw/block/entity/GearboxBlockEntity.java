package mbtw.mbtw.block.entity;

import mbtw.mbtw.DynamicMechanicalSource;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.GearboxBlock;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.util.math.DirectionHelper;
import mbtw.mbtw.util.math.Relative;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GearboxBlockEntity extends BlockEntity implements MechanicalSinkBlockEntity, MechanicalSourceBlockEntity {
    private int sink;
    private int availablePower;
    private int bearingField;

    public GearboxBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.GEARBOX_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, GearboxBlockEntity gearbox) {
        MechanicalSinkBlockEntity.mechanicalTick(world, pos, state, gearbox);
        gearbox.availablePower = gearbox.sink().getAvailablePower(world, state, pos, gearbox);
        MechanicalSourceBlockEntity.mechanicalTick(world, pos, state, gearbox);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.sink = nbt.getShort("Sink");
        this.bearingField = nbt.getShort("Bearing");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("Sink", (short)this.sink);
        nbt.putShort("Bearing", (short)this.bearingField);
    }

    @Override
    public MechanicalSink sink() {
        return (MechanicalSink) Mbtw.GEARBOX;
    }

    @Override
    public int getAvailablePower(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return availablePower;
    }

    @Override
    public int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        return sink;
    }

    @Override
    public void worldSetAvailablePower(World world, BlockPos sinkPos, BlockState sinkState, int availablePower) {
        if (this.availablePower != availablePower) {
            this.availablePower = availablePower;
            if (this.availablePower > 0) {
                this.sink = availablePower;
            }
            for (Direction direction : sink().getInputFaces(sinkState)) {
                world.updateNeighbor(sinkPos.offset(direction), sinkState.getBlock(), sinkPos);
            }
        }
    }

    @Override
    public void worldSetSink(World world, BlockPos sinkPos, BlockState sinkState, int sink) {
        if (this.sink != sink) {
            this.sink = sink;
            this.markDirty();
            for (Direction direction : sink().getInputFaces(sinkState)) {
                world.updateNeighbor(sinkPos.offset(direction), sinkState.getBlock(), sinkPos);
            }
        }
    }

    @Override
    public DynamicMechanicalSource source() {
        return (DynamicMechanicalSource) Mbtw.GEARBOX;
    }

    @Override
    public boolean getBearingAtFace(BlockState state, Direction face) {
        Relative relative = DirectionHelper.getRelativeCheckY(state.get(Properties.FACING), state.get(GearboxBlock.UP_DIRECTION), face);
        return (bearingField & relative.getBitField()) != 0;
    }

    @Override
    public void worldSetSourceBase(World world, BlockPos sourcePos, BlockState sourceState, int sourceBase) {
        BlockState newState = sourceState.with(GearboxBlock.SOURCE_BASE, sourceBase);
        world.setBlockState(sourcePos, newState, Block.NOTIFY_ALL);
    }

    @Override
    public void worldBreakConnector(World world, BlockPos connectorPos) {
        world.breakBlock(connectorPos, false);
    }

    public static int computeBearingAtFace(BlockState state, Direction face, int bearingFieldValue, boolean bearing) {
        Relative relative = DirectionHelper.getRelativeCheckY(state.get(Properties.FACING), state.get(GearboxBlock.UP_DIRECTION), face);

        int newValue;
        if (bearing) {
            // bitwise or, will always add the specific bit
            newValue = bearingFieldValue | relative.getBitField();
        } else {
            // bitwise and of state and field to switch ensures we only subtract if it is set
            newValue = bearingFieldValue - (bearingFieldValue & relative.getBitField());
        }
        return newValue;
    }

    @Override
    public void worldSetBearing(World world, BlockPos sourcePos, BlockState sourceState, List<Direction> bearingDirections) {
        int bearingFieldValue = 0;
        for (Direction direction : bearingDirections) {
            bearingFieldValue = computeBearingAtFace(sourceState, direction, bearingFieldValue, true);
        }
        if (bearingFieldValue != bearingField) {
            bearingField = bearingFieldValue;
            markDirty();
            for (Direction direction : source().getOutputFaces(sourceState)) {
                world.updateNeighbor(sourcePos.offset(direction), sourceState.getBlock(), sourcePos);
            }
        }
    }
}
