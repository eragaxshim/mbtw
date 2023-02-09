package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.MechanicalSink;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GearboxBlockEntity extends BlockEntity implements MechanicalSinkBlockEntity {
    private int sink;
    private int availablePower;

    public GearboxBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.GEARBOX_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState sinkState, GearboxBlockEntity gearbox) {
        MechanicalSinkBlockEntity.mechanicalTick(world, sinkPos, sinkState, gearbox);
        gearbox.availablePower = gearbox.sink().getAvailablePower(world, sinkState, sinkPos, gearbox);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.sink = nbt.getShort("Sink");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("Sink", (short)this.sink);
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
}
