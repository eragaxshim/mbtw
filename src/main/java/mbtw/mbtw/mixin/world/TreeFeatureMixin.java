package mbtw.mbtw.mixin.world;

import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin {
    @Inject(method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Lnet/minecraft/world/gen/feature/TreeFeatureConfig;)Z", at = @At(value = "RETURN"))
    protected void changeGenerate(StructureWorldAccess world, Random random, BlockPos pos, BiConsumer<BlockPos, BlockState> rootPlacerReplacer, BiConsumer<BlockPos, BlockState> trunkPlacerReplacer, BiConsumer<BlockPos, BlockState> foliagePlacerReplacer, TreeFeatureConfig config, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {

            for (int i = 0; i <= 1; i++) {
                for (int j = 0; j <= 1; j++) {
                    BlockPos newPos = pos.add(i, 0, j);
                    BlockState state = world.getBlockState(newPos);
                    BlockState trunkBlock = MbtwTagsMaps.LOG_TRUNK_MAP.get(state.getBlock());
                    if (trunkBlock != null) {
                        world.setBlockState(newPos, trunkBlock, Block.NOTIFY_ALL | Block.FORCE_STATE);
                    }
                }
            }
        }
    }
}
