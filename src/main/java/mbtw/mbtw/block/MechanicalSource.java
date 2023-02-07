package mbtw.mbtw.block;

import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public interface MechanicalSource {

    default int getOutRotation(World world, BlockPos sourcePos, BlockState sourceState, Vec3i sourceToSink) {
        for (MechanicalVec vec : this.getOutVecs(sourcePos, sourceState)) {
            if (vec.sameDirectionAsVec(sourceToSink)) {
                return 1;
            }
        }

        return 0;
    }

    boolean isSourceAtFace(BlockState state, Direction face);

    int getSourceAtFace(BlockState state, Direction face);

    boolean getBearingAtFace(BlockState state, Direction face);

    List<MechanicalVec> getOutVecs(BlockPos sourcePos, BlockState sourceState);

//    default Optional<BlockPos> lookForSink(World world, BlockPos sourcePos, BlockState sourceState) {
//        System.out.println("looking!");
//
//        for (MechanicalVec rotVec : this.getOutVecs(sourcePos, sourceState)) {
//            // Temporary
//            for (BlockPos iPos : BlockPos.iterate(sourcePos, sourcePos.offset(rotVec.getDirection(), 6))) {
//                BlockState iState = world.getBlockState(iPos);
//                System.out.println(iState);
//                Block iBlock = iState.getBlock();
//                if (!(iBlock instanceof MechanicalConnector)) break;
//                if (!(iBlock instanceof MechanicalSink)) continue;
//                MechanicalSink sink = MbtwApi.SINK_API.find(world, iPos, iState, null, null);
//                if (sink == null) continue;
//                if (!sink.addSource(world, rotVec, sourcePos, sourceState, iPos, iState)) continue;
//
//                return Optional.of(iPos);
//            }
//        }
//
//        return Optional.empty();
//    }
}
