package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.MechanicalConnector;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.util.SinkUpdate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MechanicalSinkBlockEntity {
    MechanicalSink sink();

    static void mechanicalTick(World world, BlockPos sinkPos, BlockState sinkState, MechanicalSinkBlockEntity sinkEntity) {

        SinkUpdate update = new SinkUpdate(world, sinkPos, sinkState, sinkEntity);
        // This only looks at bearing load
        if (!update.updateSinkPower()) {
            // This will update
            boolean bl = update.updateSink();
        };

        // This now updates connectors with new sink


    }

    int getAvailablePower(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity);

    int getSink(World world, BlockState state, BlockPos pos, @Nullable MechanicalSinkBlockEntity blockEntity);

    void worldSetAvailablePower(World world, BlockPos sinkPos, BlockState sinkState, int availablePower);

    void worldSetSink(World world, BlockPos sinkPos, BlockState sinkState, int sink);
}
