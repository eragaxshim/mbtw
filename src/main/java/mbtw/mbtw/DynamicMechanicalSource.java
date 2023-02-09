package mbtw.mbtw;

import mbtw.mbtw.block.MechanicalSource;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface DynamicMechanicalSource extends MechanicalSource {
    int getAvailableDelivery(BlockState state);

    BlockState setBearingAtFace(BlockState state, Direction face, boolean bearing);
    BlockState setSourceBase(BlockState state, int sourceBase);

    int costPerBase(BlockState state, List<Direction> includedFaces);

    int getRatioAtFace(BlockState state, Direction face);

    int getSourceBase(BlockState state);
}
