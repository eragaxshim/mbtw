package mbtw.mbtw.mixin.world;

import com.google.common.collect.ImmutableList;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(StraightTrunkPlacer.class)
public class TrunkPlacerMixin {

    @Inject(method = "generate", at = @At(value = "HEAD"), cancellable = true)
    protected void changeGenerate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config, CallbackInfoReturnable<List<FoliagePlacer.TreeNode>> cir) {
        TrunkPlacerInvoker.invokeSetToDirt(world, replacer, random, startPos.down(), config);

        Function<BlockState, BlockState> stateProvider = Function.identity();
        if (TreeFeature.canReplace(world, startPos)) {
            BlockState state = config.trunkProvider.getBlockState(random, startPos);
            BlockState trunkBlock = MbtwTagsMaps.LOG_TRUNK_MAP.get(state.getBlock());
            replacer.accept(startPos, stateProvider.apply(trunkBlock));
        }
        for (int i = 1; i < height; ++i) {
            TrunkPlacerInvoker.invokeGetAndSetState(world, replacer, random, startPos.up(i), config);
        }
        cir.setReturnValue(ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false)));
    }


}
