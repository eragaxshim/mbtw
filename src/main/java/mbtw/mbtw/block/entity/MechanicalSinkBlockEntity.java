package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.util.SinkUpdate;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MechanicalSinkBlockEntity {
    MechanicalSink sinkBlock();

    static void mechanicalTick(World world, BlockPos sinkPos, BlockState sinkState, MechanicalSinkBlockEntity sinkEntity) {

        SinkUpdate update = new SinkUpdate(world, sinkPos, sinkState, sinkEntity);
        // This only looks at bearing load
        if (!update.updateSinkPower()) {
            // This will update
            boolean bl = update.updateSink();
        };

        // This now updates connectors with new sink


    }

    int getAvailablePower();

    /**
     * Used to supply the value of getSink() for MechanicalSink
     */
    static int blockGetSink(World world, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity) {
        if (blockEntity != null) {
            return blockEntity.getSink();
        }
        if (world.getBlockEntity(pos) instanceof MechanicalSinkBlockEntity mechanicalSinkBlockEntity) {
            return mechanicalSinkBlockEntity.getSink();
        }
        return 0;
    }

    int getSink();

    void setAvailablePower(int availablePower);

    default void worldSetAvailablePower(World world, BlockState sinkState, BlockPos sinkPos, int availablePower) {
        if (!(this instanceof BlockEntity blockEntity)) throw new IllegalCallerException("MechanicalSinkBlockEntity should always extend BlockEntity!");

        if (this.getAvailablePower() != availablePower) {
            this.setAvailablePower(availablePower);
            if (availablePower > 0) {
                this.setSink(availablePower);
            }
            blockEntity.markDirty();
            for (Direction direction : sinkBlock().getInputFaces(sinkState)) {
                world.updateNeighbor(sinkPos.offset(direction), sinkState.getBlock(), sinkPos);
            }
        }
    }

    /**
     * Only called from contexts that will call markDirty if necessary.
     */
    void setSink(int sink);

    default void worldSetSink(World world, BlockState sinkState, BlockPos sinkPos, int sink) {
        if (!(this instanceof BlockEntity blockEntity)) throw new IllegalCallerException("MechanicalSinkBlockEntity should always extend BlockEntity!");

        if (this.getSink() != sink) {
            this.setSink(sink);
            blockEntity.markDirty();
            for (Direction direction : sinkBlock().getInputFaces(sinkState)) {
                world.updateNeighbor(sinkPos.offset(direction), sinkState.getBlock(), sinkPos);
            }
        }
    }
}
