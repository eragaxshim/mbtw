package mbtw.mbtw.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;
import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
public interface TrunkPlacerInvoker {
    @Invoker("setToDirt")
    static void invokeSetToDirt(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
        throw new AssertionError();
    }

    @Invoker("getAndSetState")
    static boolean invokeGetAndSetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, TreeFeatureConfig config) {
        throw new AssertionError();
    }
}
