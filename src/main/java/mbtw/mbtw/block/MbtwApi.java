package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MbtwApi {
    public static final BlockApiLookup<MechanicalSource, Void> SOURCE_API = BlockApiLookup.get(
            new Identifier(Mbtw.MOD_ID, "source_api"), MechanicalSource.class, Void.class
    );

    public static final BlockApiLookup<MechanicalSink, Void> SINK_API = BlockApiLookup.get(
            new Identifier(Mbtw.MOD_ID, "sink_api"), MechanicalSink.class, Void.class
    );

    public static MechanicalSource findSource(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        Block foundBlock = world.getBlockState(pos).getBlock();
        if (foundBlock instanceof MechanicalSource) {
            return (MechanicalSource) foundBlock;
        } else {
            return null;
        }
    }

    public static MechanicalSink findSink(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        Block foundBlock = world.getBlockState(pos).getBlock();
        if (foundBlock instanceof MechanicalSink) {
            return (MechanicalSink) foundBlock;
        } else {
            return null;
        }
    }
}
