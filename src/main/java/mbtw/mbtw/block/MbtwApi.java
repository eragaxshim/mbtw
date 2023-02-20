package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.lookup.BlockTypeApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class MbtwApi {
//    public static final BlockApiLookup<Predicate<ItemVariant>, Void> INSERT_API = BlockApiLookup.get(
//            new Identifier(Mbtw.MOD_ID, "insert_api"), filterPredicateClass(), Void.class
//    );

    public static final BlockTypeApiLookup<HopperFilter> HOPPER_FILTER_API = BlockTypeApiLookup.get(
            new Identifier(Mbtw.MOD_ID, "hopper_filter_api"), HopperFilter.class
    );

//    @SuppressWarnings("unchecked")
//    private static Class<Predicate<ItemVariant>> filterPredicateClass() {
//        return (Class<Predicate<ItemVariant>>) (Object) Predicate.class;
//    }

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
