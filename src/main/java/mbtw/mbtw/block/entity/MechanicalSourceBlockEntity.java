package mbtw.mbtw.block.entity;

import mbtw.mbtw.DynamicMechanicalSource;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.util.ConnectorState;
import mbtw.mbtw.util.SinkUpdate;
import mbtw.mbtw.util.SourceUpdate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public interface MechanicalSourceBlockEntity {


    DynamicMechanicalSource source();

    static void mechanicalTick(World world, BlockPos sourcePos, BlockState sourceState, MechanicalSourceBlockEntity sourceEntity) {
        SourceUpdate update = new SourceUpdate(world, sourcePos, sourceState, sourceEntity);
        int newSourceBase = update.updateSourceBase();
        update.updateBearing(newSourceBase);
    }

    boolean getBearingAtFace(BlockState state, Direction face);

    void worldSetSourceBase(World world, BlockPos sourcePos, BlockState sourceState, int sourceBase);

    void worldBreakConnector(World world, BlockPos connectorPos);

    void worldSetBearing(World world, BlockPos sourcePos, BlockState sourceState, List<Direction> bearingFaces);
}
